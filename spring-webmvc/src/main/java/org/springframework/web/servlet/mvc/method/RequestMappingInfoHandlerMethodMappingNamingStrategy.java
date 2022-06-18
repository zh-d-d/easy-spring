package org.springframework.web.servlet.mvc.method;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;

/**
 * A {@link HandlerMethodMappingNamingStrategy} for RequestMappingInfo-based handler method
 * mapping. If the RequestMappingInfo name attribute is set, its value is used.
 * Otherwise the name is based on the capital letters of the class name, followed by
 * "#" as a separator, and the method name. For example "TC#getFoo" for a class named
 * TestController with method getFoo.
 *
 * @author zhangdd on 2022/6/18
 */
public class RequestMappingInfoHandlerMethodMappingNamingStrategy
        implements HandlerMethodMappingNamingStrategy<RequestMappingInfo> {

    public static final String SEPARATOR = "#";

    @Override
    public String getName(HandlerMethod handlerMethod, RequestMappingInfo mapping) {
        if (null != mapping.getName()) {
            return mapping.getName();
        }
        StringBuilder sb = new StringBuilder();
        String simpleTypeName = handlerMethod.getBeanType().getSimpleName();
        for (int i = 0; i < simpleTypeName.length(); i++) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }
        sb.append(SEPARATOR).append(handlerMethod.getMethod().getName());
        return sb.toString();
    }
}
