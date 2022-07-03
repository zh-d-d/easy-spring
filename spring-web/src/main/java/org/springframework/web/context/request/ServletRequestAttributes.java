package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangdd on 2022/7/2
 */
public class ServletRequestAttributes extends AbstractRequestAttributes {

    private final HttpServletRequest request;

    @Nullable
    private HttpServletResponse response;


    public ServletRequestAttributes(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        this.request = request;
    }

    public ServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
        this(request);
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Nullable
    public HttpServletResponse getResponse() {
        return response;
    }

}
