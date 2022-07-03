package org.springframework.http.server;

import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangdd on 2022/7/3
 */
public class ServletServerHttpRequest {

    private final HttpServletRequest servletRequest;

    public ServletServerHttpRequest(HttpServletRequest servletRequest) {
        Assert.notNull(servletRequest, "HttpServletRequest must not be null");
        this.servletRequest = servletRequest;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }
}
