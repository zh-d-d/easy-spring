package org.springframework.web.servlet.mvc.method.support;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Extension of {@link HandlerMethod} that invokes the underlying method with argument values
 * resolved form the current HTTP request through a list of {@link HandlerMethodArgumentResolver}.
 *
 * @author zhangdd on 2022/7/2
 */
public class InvocableHandlerMethod extends HandlerMethod {

    private static final Object[] EMPTY_ARGS = new Object[0];

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    /**
     * Invoke the method after resolving its argument values in the context of the
     * given request.
     * <p>
     * Argument values are commonly resolved through {@link HandlerMethodArgumentResolver}.
     */
    @Nullable
    public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
                                   Object... provideArgs) throws Exception {

        Object[] args = getMethodArgumentValues(request, mavContainer, provideArgs);
        return doInvoke(args);
    }

    protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
                                               Object... provideArgs) throws Exception {
        return EMPTY_ARGS;
    }

    @Nullable
    protected Object doInvoke(Object... args) {
        Method method = getBridgedMethod();
        try {
            return method.invoke(getBean(), args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
