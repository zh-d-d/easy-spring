package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.support.InvocableHandlerMethod;

import java.lang.reflect.Method;

/**
 * Extends {@link InvocableHandlerMethod} with the ability to handle return
 * values through a registered {@link HandlerMethodReturnValueHandler} and
 * also supports setting the response status based on a method-level
 * {@code @ResponseStatus} annotation.
 *
 * @author zhangdd on 2022/7/2
 */
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {

    @Nullable
    private HandlerMethodReturnValueHandler returnValueHandlers;

    public ServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public ServletInvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }


    public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
                                Object... provideArgs) throws Exception {
        //这里会进行业务方法的实际调用
        Object returnValue = invokeForRequest(webRequest, mavContainer, provideArgs);

        Assert.state(this.returnValueHandlers != null, "No return value handlers");
        this.returnValueHandlers.handleReturnValue(
                returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
    }
}
