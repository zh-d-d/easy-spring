package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangdd on 2022/7/9
 */
public abstract class AbstractJackson2HttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    private static final Map<String, JsonEncoding> ENCODINGS;

    static {
        ENCODINGS = CollectionUtils.newHashMap(JsonEncoding.values().length);
        for (JsonEncoding encoding : JsonEncoding.values()) {
            ENCODINGS.put(encoding.getJavaName(), encoding);
        }
        ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
    }

    protected ObjectMapper defaultObjectMapper;

    @Nullable
    private PrettyPrinter ssePrettyPrinter;

    @Nullable
    private Map<Class<?>, Map<MediaType, ObjectMapper>> objectMapperRegistrations;

    public AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        this.defaultObjectMapper = objectMapper;
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper,MediaType...supportedMediaTypes){
        this(objectMapper);
        setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }


    @Nullable
    public Map<Class<?>, Map<MediaType, ObjectMapper>> getObjectMapperRegistrations() {
        return null != this.objectMapperRegistrations ? this.objectMapperRegistrations : Collections.emptyMap();
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return canRead(clazz, null, mediaType);
    }

    @Override
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!canRead(mediaType)) {
            return false;
        }
        JavaType javaType = getJavaType(type, contextClass);
        ObjectMapper objectMapper = selectObjectMapper(javaType.getRawClass(), mediaType);
        if (null == objectMapper) {
            return false;
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (objectMapper.canDeserialize(javaType, causeRef)) {
            return true;
        }
        logWarningIfNecessary(javaType, causeRef.get());
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!canWrite(mediaType)) {
            return false;
        }
        if (null != mediaType && null != mediaType.getCharset()) {
            Charset charset = mediaType.getCharset();
            if (ENCODINGS.containsKey(charset.name())) {
                return false;
            }
        }
        ObjectMapper objectMapper = selectObjectMapper(clazz, mediaType);
        if (null == objectMapper) {
            return false;
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (objectMapper.canSerialize(clazz, causeRef)) {
            return true;
        }
        logWarningIfNecessary(clazz, causeRef.get());
        return false;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotWritableException {
        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotWritableException {
        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = getJsonEncoding(contentType);

        Class<?> clazz = object.getClass();
        ObjectMapper objectMapper = selectObjectMapper(clazz, contentType);
        Assert.state(null != objectMapper, "No ObjectMapper for " + clazz.getName());
        OutputStream outputStream = StreamUtils.nonClosing(outputMessage.getBody());
        try (JsonGenerator generator = objectMapper.getFactory().createGenerator(outputStream, encoding)) {

            ObjectWriter objectWriter = objectMapper.writer();
            SerializationConfig config = objectWriter.getConfig();
            if (null != contentType && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) &&
                    config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.with(this.ssePrettyPrinter);
            }
            objectWriter.writeValue(generator, object);

            generator.flush();
        }

    }

    @Nullable
    private ObjectMapper selectObjectMapper(Class<?> targetType, @Nullable MediaType targetMediaType) {
        if (null == targetMediaType || CollectionUtils.isEmpty(this.objectMapperRegistrations)) {
            return this.defaultObjectMapper;
        }
        for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> typeEntry : getObjectMapperRegistrations().entrySet()) {
            if (typeEntry.getKey().isAssignableFrom(targetType)) {
                for (Map.Entry<MediaType, ObjectMapper> objectMapperEntry : typeEntry.getValue().entrySet()) {
                    if (objectMapperEntry.getKey().includes(targetMediaType)) {
                        return objectMapperEntry.getValue();
                    }
                }
                //No matching registrations
                return null;
            }
        }
        //No registrations
        return this.defaultObjectMapper;
    }

    /**
     * Return the Jackson {@link JavaType} for the specified type and context class.
     */
    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        return this.defaultObjectMapper.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    protected JsonEncoding getJsonEncoding(@Nullable MediaType contentType) {
        if (null != contentType && null != contentType.getCharset()) {
            Charset charset = contentType.getCharset();
            JsonEncoding encoding = ENCODINGS.get(charset.name());
            if (null != encoding) {
                return encoding;
            }
        }
        return JsonEncoding.UTF8;
    }

    protected void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
        if (cause == null) {
            return;
        }

        // Do not log warning for serializer not found (note: different message wording on Jackson 2.9)
        boolean debugLevel = (cause instanceof JsonMappingException && cause.getMessage().startsWith("Cannot find"));

        if (debugLevel ? logger.isDebugEnabled() : logger.isWarnEnabled()) {
            String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") +
                    "serialization for type [" + type + "]";
            if (debugLevel) {
                logger.debug(msg, cause);
            } else if (logger.isDebugEnabled()) {
                logger.warn(msg, cause);
            } else {
                logger.warn(msg + ": " + cause);
            }
        }
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = getCharset(contentType);
        ObjectMapper objectMapper = selectObjectMapper(javaType.getRawClass(), contentType);
        Assert.state(null != objectMapper, "No ObjectMapper for " + javaType);
        boolean isUnicode = ENCODINGS.containsKey(charset.name()) ||
                "UTF-16".equals(charset.name()) ||
                "UTF-32".equals(charset.name());

        if (isUnicode) {
            return objectMapper.readValue(inputMessage.getBody(), javaType);
        } else {
            Reader reader = new InputStreamReader(inputMessage.getBody(), charset);
            return objectMapper.readValue(reader, javaType);
        }
    }

    protected Charset getCharset(@Nullable MediaType contentType) {
        if (null != contentType && null != contentType.getCharset()) {
            return contentType.getCharset();
        } else {
            return StandardCharsets.UTF_8;
        }
    }
}
