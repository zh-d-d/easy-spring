package org.springframework.http.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * A specialization of {@link HttpMessageConverter} that can convert an HTTP request into a
 * target object of a specified generic type and a source object of a specified
 * generic type into an HTTP response.
 *
 * @author zhangdd on 2022/7/9
 */
public interface GenericHttpMessageConverter<T> extends HttpMessageConverter<T> {

    boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType);

    T read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotWritableException;

    boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType);

    void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException;
}
