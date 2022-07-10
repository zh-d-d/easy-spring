package org.springframework.http.converter;

import org.apache.commons.logging.Log;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for most {@link HttpMessageConverter} implementations.
 * <p>
 * The base class adds support for setting supported MediaTypes, through the
 * {@link #setSupportedMediaTypes(List)} bean property. It also adds
 * support for {@code Content-Type} and {@code Content-length} when writing to output messages.
 *
 * @author zhangdd on 2022/7/9
 */
public abstract class AbstractHttpMessageConverter<T> implements HttpMessageConverter<T> {

    /**
     * Logger available to subclasses.
     */
    protected final Log logger = HttpLogging.forLogName(getClass());

    private List<MediaType> supportedMediaTypes = Collections.emptyList();

    /**
     * Indicates whether the given class is supported by this converter.
     */
    protected abstract boolean supports(Class<?> clazz);

    /**
     * Abstract template method that reads the actual object. Invoke from {@link #read(Class, HttpInputMessage)}
     */
    protected abstract T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotWritableException;

    /**
     * Abstract template method that writes the actual body. Invoke from {@link #write(Object, MediaType, HttpOutputMessage)}
     */
    protected abstract void writeInternal(T t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException;

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return supports(clazz) && canRead(mediaType);
    }

    /**
     * Return true if any of the {@linkplain #setSupportedMediaTypes(List)}
     * media types {@link MediaType#includes(MimeType)} the
     * given media type.
     */
    protected boolean canRead(@Nullable MediaType mediaType) {
        if (null == mediaType) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return supports(clazz) && canWrite(mediaType);
    }

    /**
     * Return true if the given media type include any of the
     * {@linkplain #setSupportedMediaTypes(List)}
     */
    protected boolean canWrite(@Nullable MediaType mediaType) {
        if (null == mediaType || MediaType.ALL.equalsTypeAndSubtype(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotWritableException {
        return readInternal(clazz, inputMessage);
    }

    @Override
    public void write(T t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal(t, outputMessage);
        outputMessage.getBody().flush();
    }
}
