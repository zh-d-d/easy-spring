package org.springframework.web.method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

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

    private final Method method;

    private final Class<?> beanType;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.beanType = ClassUtils.getUserClass(bean);
    }

    private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        Assert.notNull(handler, "Handler object is required");
        this.bean = handler;
        this.method = handlerMethod.method;
        this.beanType = handlerMethod.beanType;
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


    public HandlerMethod createWithResolvedBean() {

        return new HandlerMethod(this, this.bean);
    }
}
