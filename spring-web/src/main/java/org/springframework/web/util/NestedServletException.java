package org.springframework.web.util;

import org.springframework.core.NestedExceptionUtils;

import javax.servlet.ServletException;

/**
 * @author zhangdd on 2022/6/11
 */
public class NestedServletException extends ServletException {

    public NestedServletException(String message) {
        super(message);
    }

    public NestedServletException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    @Override
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }

}
