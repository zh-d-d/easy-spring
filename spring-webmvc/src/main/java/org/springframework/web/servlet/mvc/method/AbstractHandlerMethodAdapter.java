package org.springframework.web.servlet.mvc.method;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangdd on 2022/7/2
 */
public abstract class AbstractHandlerMethodAdapter implements HandlerAdapter {


    protected abstract boolean supportsInternal(HandlerMethod handlerMethod);

    @Nullable
    protected abstract ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception;


    public AbstractHandlerMethodAdapter() {
    }

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMethod && supportsInternal(((HandlerMethod) handler)));
    }


    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return handleInternal(request, response, ((HandlerMethod) handler));
    }
}
