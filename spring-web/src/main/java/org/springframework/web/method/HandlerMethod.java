package org.springframework.web.method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Encapsulates information about a handler method consisting of a
 * {@linkplain #getMethod() method} and a {@linkplain #getBean() bean}.
 * Provides convenient access to method parameters, the method return value,
 * method annotations, etc.
 *
 * @author zhangdd on 2022/6/18
 */
public class HandlerMethod {

    protected static final Log logger = LogFactory.getLog(HandlerMethod.class);

    private final Object bean;

    @Nullable
    private final BeanFactory beanFactory;

    private final Class<?> beanType;

    private final Method method;

    protected final Method bridgedMethod;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.beanFactory = null;
        this.method = method;
        this.beanType = ClassUtils.getUserClass(bean);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(this.bridgedMethod);
    }

    public HandlerMethod(String beanName, BeanFactory beanFactory,
                         @Nullable MessageSource messageSource, Method method) {
        Assert.hasText(beanName, "Bean name is required");
        Assert.notNull(beanFactory, "BeanFactory is required");
        Assert.notNull(method, "Method is required");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        Class<?> beanType = beanFactory.getType(beanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot resolve bean type for bean with name '" + beanName + "'");
        }
        this.beanType = ClassUtils.getUserClass(beanType);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(this.bridgedMethod);
    }

    protected HandlerMethod(HandlerMethod handlerMethod) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        this.bean = handlerMethod.bean;
        this.method = handlerMethod.method;
        this.beanType = handlerMethod.beanType;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.beanFactory = handlerMethod.beanFactory;
    }

    private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        Assert.notNull(handler, "Handler object is required");
        this.bean = handler;
        this.method = handlerMethod.method;
        this.beanType = handlerMethod.beanType;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.beanFactory = handlerMethod.beanFactory;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public Method getBridgedMethod() {
        return bridgedMethod;
    }

    public MethodParameter getReturnValueType(@Nullable Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    public HandlerMethod createWithResolvedBean() {
        Object handler = this.bean;
        if (this.bean instanceof String) {
            Assert.state(this.beanFactory != null, "Cannot resolve bean name without BeanFactory");
            String beanName = ((String) this.bean);
            handler = this.beanFactory.getBean(beanName);
        }

        return new HandlerMethod(this, handler);
    }

    protected class HandlerMethodParameter extends SynthesizingMethodParameter {
        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        protected HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }
    }

    private class ReturnValueMethodParameter extends HandlerMethodParameter {
        @Nullable
        private final Object returnValue;

        public ReturnValueMethodParameter(@Nullable Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
        }

        @Override
        public Class<?> getParameterType() {
            return (this.returnValue != null ? this.returnValue.getClass() : super.getParameterType());
        }

        @Override
        public ReturnValueMethodParameter clone() {
            return new ReturnValueMethodParameter(this);
        }
    }
}
