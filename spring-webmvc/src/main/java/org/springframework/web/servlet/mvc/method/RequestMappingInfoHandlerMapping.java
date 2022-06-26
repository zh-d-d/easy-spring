package org.springframework.web.servlet.mvc.method;

import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author zhangdd on 2022/6/18
 */
public abstract class RequestMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<RequestMappingInfo> {


    @Override
    protected Set<String> getDirectPaths(RequestMappingInfo info) {
        return info.getDirectPaths();
    }

    @Override
    protected RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
        return info.getMatchingCondition(request);
    }
}
