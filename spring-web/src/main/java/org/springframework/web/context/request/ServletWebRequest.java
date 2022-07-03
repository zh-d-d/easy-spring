package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangdd on 2022/7/2
 */
public class ServletWebRequest extends ServletRequestAttributes implements NativeWebRequest {


    public ServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public <T> T getNativeRequest(Class<T> requiredType) {
        return WebUtils.getNativeRequest(getRequest(), requiredType);
    }

    @Override
    public <T> T getNativeResponse(Class<T> requiredType) {
        HttpServletResponse response = getResponse();
        return null != response ? WebUtils.getNativeResponse(response, requiredType) : null;
    }
}
