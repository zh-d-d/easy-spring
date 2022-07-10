package org.springframework.web;

import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangdd on 2022/7/3
 */
public abstract class HttpMediaTypeException extends ServletException {
    private final List<MediaType> supportedMediaTypes;

    /**
     * Create a new HttpMediaTypeException.
     *
     * @param message the exception message
     */
    protected HttpMediaTypeException(String message) {
        super(message);
        this.supportedMediaTypes = Collections.emptyList();
    }

    /**
     * Create a new HttpMediaTypeException with a list of supported media types.
     *
     * @param supportedMediaTypes the list of supported media types
     */
    protected HttpMediaTypeException(String message, List<MediaType> supportedMediaTypes) {
        super(message);
        this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
    }
}
