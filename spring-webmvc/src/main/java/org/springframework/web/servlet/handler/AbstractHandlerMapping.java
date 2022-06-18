package org.springframework.web.servlet.handler;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.support.WebApplicationObjectSupport;


/**
 * @author zhangdd on 2022/6/15
 */
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport {


    private PathMatcher pathMatcher = new AntPathMatcher();


    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }
}
