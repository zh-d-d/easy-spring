package org.springframework.http.converter;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * @author zhangdd on 2022/7/3
 */
public class HttpMessageConversionException extends NestedRuntimeException {

    /**
     * Create a new HttpMessageConversionException.
     * @param msg the detail message
     */
    public HttpMessageConversionException(String msg) {
        super(msg);
    }

    /**
     * Create a new HttpMessageConversionException.
     * @param msg the detail message
     * @param cause the root cause (if any)
     */
    public HttpMessageConversionException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

}
