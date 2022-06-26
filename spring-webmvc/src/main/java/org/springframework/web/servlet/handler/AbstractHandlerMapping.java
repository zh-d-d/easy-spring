package org.springframework.web.servlet.handler;

import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.http.HttpServletRequest;


/**
 * @author zhangdd on 2022/6/15
 */
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport
        implements HandlerMapping {

    /**
     * Look up a handler for the given request, returning null if no specific one is
     * found.
     */
    protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;


    @Nullable
    private PathPatternParser patternParser;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private PathMatcher pathMatcher = new AntPathMatcher();


    public void setPatternParser(@Nullable PathPatternParser patternParser) {
        this.patternParser = patternParser;
    }

    @Nullable
    public PathPatternParser getPatternParser() {
        return patternParser;
    }

    public UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }


    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = getHandlerInternal(request);

        return getHandlerExecutionChain(handler, request);
    }

    /**
     * Initialize the path to use for request mapping.
     */
    protected String initLookupPath(HttpServletRequest request) {
        return getUrlPathHelper().resolvedAndCacheLookupPath(request);
    }

    /**
     * Build a {@link HandlerExecutionChain} for the given handler, including
     * applicable interceptors.
     */
    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {

        return handler instanceof HandlerExecutionChain ?
                (HandlerExecutionChain) handler :
                new HandlerExecutionChain(handler);
    }
}
