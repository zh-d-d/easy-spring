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
     * Name of the {@link HttpServletRequest} attribute that contains the set of
     * producible MediaTypes applicable to the mapped handler.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. Handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE = HandlerMapping.class.getName() + ".producibleMediaTypes";

    /**
     * Return a handler and any interceptors for this request.
     */
    @Nullable
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
