package org.springframework.web;

import javax.servlet.ServletException;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangdd on 2022/7/3
 */
public abstract class HttpMediaTypeException extends ServletException {


    /**
     * Create a new HttpMediaTypeException.
     *
     * @param message the exception message
     */
    protected HttpMediaTypeException(String message) {
        super(message);

    }

}
