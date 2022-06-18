package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * @author zhangdd on 2022/6/18
 */
public class RequestMappingHandlerMapping extends RequestMappingInfoHandlerMapping {

    /**
     * Whether the given type is a handler with handler methods.
     * <p>
     * Expects a handler to have either a type-level {@link Controller}
     * annotation or a type-level {@link RequestMapping} annotation.
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class);
    }


    /**
     * Use method and type-level {@link RequestMapping} annotations to create
     * the RequestMappingInfo.
     */
    @Override
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (null != info) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (null != typeInfo) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    @Nullable
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) :
                getCustomMethodCondition((Method) element);
        return null != requestMapping ? createRequestMappingInfo(requestMapping, condition) : null;
    }

    @Nullable
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return null;
    }

    @Nullable
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return null;
    }

    /**
     * Create a {@link RequestMappingInfo} from the supplied {@link RequestMapping} annotation, which
     * is either a directly declared annotation, a meta-annotation, or the
     * synthesized result of merging annotation attributes within an annotation
     * hierarchy.
     */
    protected RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(requestMapping.path())
                .mappingName(requestMapping.name());

        if (null!=customCondition){
            builder.customCondition(customCondition);
        }
        return builder.build();
    }
}
