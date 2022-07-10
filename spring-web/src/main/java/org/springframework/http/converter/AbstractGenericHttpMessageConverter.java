package org.springframework.http.converter;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author zhangdd on 2022/7/9
 */
public abstract class AbstractGenericHttpMessageConverter<T> extends AbstractHttpMessageConverter<T>
        implements GenericHttpMessageConverter<T> {

    protected abstract void writeInternal(T t, @Nullable Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException;

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        return type instanceof Class ? canRead(((Class<?>) type), mediaType) : canRead(mediaType);
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return canWrite(clazz, mediaType);
    }

    @Override
    public void write(T t, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal(t, type, outputMessage);
        outputMessage.getBody().flush();
    }

    @Override
    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal(t, null, outputMessage);
    }
}
