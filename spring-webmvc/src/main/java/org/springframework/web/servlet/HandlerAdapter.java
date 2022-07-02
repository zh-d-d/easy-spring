package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangdd on 2022/7/2
 */
public interface HandlerAdapter {

    /**
     * Given a handler instance, return whether or not this HandlerAdapter can support
     * it. Typical HandlerAdapter will base the decision on the handler type.
     * HandlerAdapters will usually only support one handler type each.
     */
    boolean supports(Object handler);

    /**
     * Use the given handler to handle this request.The workflow that is required
     * may vary widely.
     */
    @Nullable
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
}
