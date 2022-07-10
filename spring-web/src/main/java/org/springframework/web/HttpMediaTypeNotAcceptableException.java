package org.springframework.web;

import org.springframework.http.MediaType;

import java.util.List;

/**
 * @author zhangdd on 2022/7/3
 */
public class HttpMediaTypeNotAcceptableException extends HttpMediaTypeException {

    /**
     * Create a new HttpMediaTypeNotAcceptableException.
     *
     * @param message the exception message
     */
    public HttpMediaTypeNotAcceptableException(String message) {
        super(message);
    }

    /**
     * Create a new HttpMediaTypeNotSupportedException.
     *
     * @param supportedMediaTypes the list of supported media types
     */
    public HttpMediaTypeNotAcceptableException(List<MediaType> supportedMediaTypes) {
        super("Could not find acceptable representation", supportedMediaTypes);
    }
}
