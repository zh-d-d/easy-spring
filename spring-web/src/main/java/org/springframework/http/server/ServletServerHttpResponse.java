package org.springframework.http.server;

import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangdd on 2022/7/3
 */
public class ServletServerHttpResponse {

    private final HttpServletResponse servletResponse;

    public ServletServerHttpResponse(HttpServletResponse servletResponse) {
        Assert.notNull(servletResponse, "HttpServletResponse must not be null");
        this.servletResponse = servletResponse;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }
}
