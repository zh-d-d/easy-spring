package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

/**
 * A strategy for assigning a name to a handler method's mapping.
 *
 * @author zhangdd on 2022/6/18
 */
public interface HandlerMethodMappingNamingStrategy<T> {

    /**
     * Determine the name for the given HandlerMethod and mapping.
     */
    String getName(HandlerMethod handlerMethod, T mapping);
}
