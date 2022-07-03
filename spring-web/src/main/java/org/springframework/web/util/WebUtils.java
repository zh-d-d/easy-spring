package org.springframework.web.util;

import org.springframework.lang.Nullable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

/**
 * @author zhangdd on 2022/6/19
 */
public abstract class WebUtils {

    public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";

    public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";

    public static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";


    public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getNativeRequest(ServletRequest request, @Nullable Class<T> requiredType) {
        if (null != requiredType) {
            if (requiredType.isInstance(request)) {
                return ((T) request);
            } else if (request instanceof ServletRequestWrapper) {
                return getNativeRequest(((ServletRequestWrapper) request).getRequest(), requiredType);
            }

        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getNativeResponse(ServletResponse response, @Nullable Class<T> requiredType) {
        if (null != requiredType) {
            if (requiredType.isInstance(response)) {
                return (T) response;
            } else if (response instanceof ServletResponseWrapper) {
                return getNativeResponse(((ServletResponseWrapper) response).getResponse(), requiredType);
            }
        }
        return null;
    }
}
