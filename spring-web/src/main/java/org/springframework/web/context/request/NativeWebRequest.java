package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/**
 * @author zhangdd on 2022/7/2
 */
public interface NativeWebRequest extends WebRequest {

    @Nullable
    String[] getHeaderValues(String headerName);

    /**
     * Return the underlying native request object, if available.
     */
    @Nullable
    <T> T getNativeRequest(@Nullable Class<T> requiredType);

    /**
     * Return the underlying response object,if available.
     */
    @Nullable
    <T> T getNativeResponse(@Nullable Class<T> requiredType);
}
