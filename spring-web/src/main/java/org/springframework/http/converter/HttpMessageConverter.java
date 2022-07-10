package org.springframework.http.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Strategy interface for converting from and to HTTP requests and responses.
 *
 * @author zhangdd on 2022/7/9
 */
public interface HttpMessageConverter<T> {

    /**
     * Indicates whether the given class can be read by this converter.
     */
    boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);

    /**
     * Indicates whether the given class can be written by this converter.
     */
    boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);


    /**
     * Return the list of media types supported by this converter. The list may not
     * apply to every possible target element type and calls to this method should
     * typically be guarded via {@link #canWrite(Class, MediaType)}. The list may also exclude MIME
     * types supported only for a specific class. Alternatively, use
     * {@link #getSupportedMediaTypes(Class)} for a more precise list.
     */
    List<MediaType> getSupportedMediaTypes();

    /**
     * Return the list of media types supported by this converter for the given
     * class. The list may differ from {@link #getSupportedMediaTypes()} if the
     * converter does not support the given Class or if it supports it only for
     * a subset of media types.
     */
    default List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        return (canRead(clazz, null) || canWrite(clazz, null) ?
                getSupportedMediaTypes() : Collections.emptyList());
    }

    /**
     * Read an object of the given type from the given input message, and return it.
     */
    T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotWritableException;

    /**
     * Write an given object to the given output message.
     */
    void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException;
}
