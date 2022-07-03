package org.springframework.http.converter;

import org.springframework.lang.Nullable;

/**
 * @author zhangdd on 2022/7/3
 */
public class HttpMessageNotWritableException extends HttpMessageConversionException {

    /**
     * Create a new HttpMessageNotWritableException.
     * @param msg the detail message
     */
    public HttpMessageNotWritableException(String msg) {
        super(msg);
    }

    /**
     * Create a new HttpMessageNotWritableException.
     * @param msg the detail message
     * @param cause the root cause (if any)
     */
    public HttpMessageNotWritableException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

}