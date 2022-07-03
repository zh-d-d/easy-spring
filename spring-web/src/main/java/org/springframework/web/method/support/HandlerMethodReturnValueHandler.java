package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Strategy interface to handle the value returned from the invocation of a handler
 * method.
 *
 * @author zhangdd on 2022/7/3
 */
public interface HandlerMethodReturnValueHandler {

    /**
     * Whether the given {@link MethodParameter method return type} is
     * supported by this handler.
     */
    boolean supportsReturnType(MethodParameter returnType);


    /**
     * Handle the given return value by adding attribute to the model and
     * setting a view or setting the
     * {@link ModelAndViewContainer#setRequestHandled} flag to {@code true}
     * to indicate the response has been handled directly.
     */
    void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                           ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception;
}
