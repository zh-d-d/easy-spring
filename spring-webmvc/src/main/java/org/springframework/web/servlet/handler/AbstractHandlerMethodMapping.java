package org.springframework.web.servlet.handler;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zhangdd on 2022/6/15
 */
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {

    /**
     * Weather the given type is a handler with handler methods.
     */
    protected abstract boolean isHandler(Class<?> beanType);

    /**
     * Provide the mapping for a handler method. A method for which no mapping can be
     * provided is not a handler method.
     */
    protected abstract T getMappingForMethod(Method method, Class<?> handlerType);

    /**
     * Bean name prefix for target beans behind scoped proxies. Used to exclude those
     * targets form handler method detection, in favor of the corresponding proxies.
     */
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";


    @Nullable
    private HandlerMethodMappingNamingStrategy<T> namingStrategy;

    private final MappingRegistry mappingRegistry = new MappingRegistry();


    public void setNamingStrategy(@Nullable HandlerMethodMappingNamingStrategy<T> namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    @Nullable
    public HandlerMethodMappingNamingStrategy<T> getNamingStrategy() {
        return namingStrategy;
    }

    /**
     * Detects handler methods at initialization
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initHandlerMethods();
    }


    /**
     * Scan beans in ApplicationContext, detect and register handler methods.
     */
    protected void initHandlerMethods() {
        for (String beanName : getCandidateBeanNames()) {
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                processCandidateBean(beanName);
            }
        }
    }

    /**
     * Determine the name of candidate beans in the application context
     */
    protected String[] getCandidateBeanNames() {
        return obtainApplicationContext().getBeanNamesForType(Object.class);
    }

    /**
     * Determine the type of the specified candidate bean and call
     * {@link #detectHandlerMethods} if identified as a handler type.
     */
    protected void processCandidateBean(String beanName) {
        Class<?> beanType = null;

        try {
            beanType = obtainApplicationContext().getType(beanName);
        } catch (Throwable ex) {
            if (logger.isTraceEnabled()) {
                logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
            }
        }
        if (beanType != null && isHandler(beanType)) {
            detectHandlerMethods(beanName);
        }
    }

    /**
     * Look for handler methods in the specified handler bean.
     */
    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType = (handler instanceof String) ? obtainApplicationContext().getType(((String) handler)) : handler.getClass();

        if (null != handlerType) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType, (MethodIntrospector.MetadataLookup<T>)
                    method -> getMappingForMethod(method, handlerType));

            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }
    }

    /**
     * Register a handler method and its unique mapping, Invoked at startup for each
     * detected handler method.
     */
    protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    protected HandlerMethod createHandlerMethod(Object handler, Method method) {
        return new HandlerMethod(handler, method);
    }

    protected Set<String> getMappingPathPatterns(T mapping) {
        return Collections.emptySet();
    }


    protected Set<String> getDirectPaths(T mapping) {
        Set<String> urls = Collections.emptySet();
        for (String path : getMappingPathPatterns(mapping)) {
            if (!getPathMatcher().isPattern(path)) {
                urls = (urls.isEmpty() ? new HashSet<>(1) : urls);
                urls.add(path);
            }
        }
        return urls;
    }

    class MappingRegistry {

        private final Map<T, MappingRegistration<T>> registry = new HashMap<>();

        private final MultiValueMap<String, T> pathLookup = new LinkedMultiValueMap<>();

        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap<>();


        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        public Map<T, MappingRegistration<T>> getRegistry() {
            return registry;
        }

        @Nullable
        public List<T> getMappingsByDirectPath(String urlPath) {
            return this.pathLookup.get(urlPath);
        }


        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();

            try {
                HandlerMethod handlerMethod = createHandlerMethod(handler, method);
                validateMethodMapping(handlerMethod, mapping);
                Set<String> directPaths = AbstractHandlerMethodMapping.this.getDirectPaths(mapping);
                for (String path : directPaths) {
                    this.pathLookup.add(path, mapping);
                }
                String name = null;
                if (null != getNamingStrategy()) {
                    name = getNamingStrategy().getName(handlerMethod, mapping);
                    addMappingName(name, handlerMethod);
                }

                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directPaths, name, false));
            } finally {
                this.readWriteLock.writeLock().unlock();
            }

        }

        private void validateMethodMapping(HandlerMethod handlerMethod, T mapping) {
            MappingRegistration<T> registration = this.registry.get(mapping);
            HandlerMethod existingHandlerMethod = null != registration ? registration.getHandlerMethod() : null;
            if (null != existingHandlerMethod && !existingHandlerMethod.equals(handlerMethod)) {
                throw new IllegalStateException("Ambiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" + handlerMethod + "\nto " + mapping + ": There is already '" + existingHandlerMethod.getBean() + "' bean method\n" + existingHandlerMethod + " mapped.");
            }
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (null == oldList) {
                oldList = Collections.emptyList();
            }
            for (HandlerMethod current : oldList) {
                if (handlerMethod.equals(current)) {
                    return;
                }
            }
            List<HandlerMethod> newList = new ArrayList<>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);
        }
    }

    static class MappingRegistration<T> {

        private final T mapping;

        private final HandlerMethod handlerMethod;

        private final Set<String> directPaths;

        @Nullable
        private final String mappingName;

        private final boolean corsConfig;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod, Set<String> directPaths, @Nullable String mappingName, boolean corsConfig) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directPaths = directPaths;
            this.mappingName = mappingName;
            this.corsConfig = corsConfig;
        }

        public T getMapping() {
            return mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return handlerMethod;
        }

        public Set<String> getDirectPaths() {
            return directPaths;
        }

        @Nullable
        public String getMappingName() {
            return mappingName;
        }

        public boolean hasCorsConfig() {
            return this.corsConfig;
        }
    }
}
