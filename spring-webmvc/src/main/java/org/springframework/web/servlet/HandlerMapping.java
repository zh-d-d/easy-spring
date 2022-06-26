package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to be implemented by objects that define a mapping between
 * requests and handler objects.
 *
 * @author zhangdd on 2022/6/19
 */
public interface HandlerMapping {

    String BEST_MATCHING_HANDLER_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingHandler";


    String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = HandlerMapping.class.getName() + ".pathWithinHandlerMapping";

    /**
     * Return a handler and any interceptors for this request.
     */
    @Nullable
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
