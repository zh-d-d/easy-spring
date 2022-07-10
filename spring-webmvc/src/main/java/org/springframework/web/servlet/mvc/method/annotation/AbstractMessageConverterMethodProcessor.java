package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author zhangdd on 2022/7/3
 */
public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver implements HandlerMethodReturnValueHandler {


    private static final List<MediaType> ALL_APPLICATION_MEDIA_TYPES =
            Arrays.asList(MediaType.ALL, new MediaType("application"));


    private final ContentNegotiationManager contentNegotiationManager;


    protected AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters) {
        this(converters, null, null);
    }

    protected AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters,
                                                      @Nullable ContentNegotiationManager manager, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
        this.contentNegotiationManager = null != manager ? manager : new ContentNegotiationManager();

    }


    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        return new ServletServerHttpRequest(servletRequest);
    }


    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        return new ServletServerHttpResponse(response);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> void writeWithMessageConverters(@Nullable T value, MethodParameter returnType,
                                                  ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage)
            throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

        Object body;
        Class<?> valueType;
        Type targetType;

        if (value instanceof CharSequence) {
            body = value.toString();
            valueType = String.class;
            targetType = String.class;
        } else {
            body = value;
            valueType = getReturnValueType(body, returnType);
            targetType = GenericTypeResolver.resolveType(getGenericType(returnType), returnType.getContainingClass());
        }

        //确定一个最合适的content-type作为selectedMediaType
        MediaType selectMediaType = null;

        MediaType contentType = outputMessage.getHeaders().getContentType();
        boolean isContentTypePreset = null != contentType && contentType.isConcrete();

        //如果@RequestMapping中的produces配置了content-type，则获取服务器端指定的content-type使用此content-type
        if (isContentTypePreset) {
            selectMediaType = contentType;
        } else {
            //如果没有

            HttpServletRequest request = inputMessage.getServletRequest();
            List<MediaType> acceptableTypes;
            try {
                //获取客户端Accept字段接收的content-type
                acceptableTypes = getAcceptableMediaTypes(request);
            } catch (HttpMediaTypeNotAcceptableException e) {
                throw new RuntimeException(e);
            }

            //获取所有HttpMessageConverter所支持的content-type
            List<MediaType> producibleTypes = getProducibleMediaTypes(request, valueType, targetType);
            if (null != body && producibleTypes.isEmpty()) {
                throw new HttpMessageNotWritableException(
                        "No converter found for return value of type: " + valueType);
            }

            //然后通过acceptableTypes 和producibleMediaTypes 比较得到 mediaTypesToUse
            List<MediaType> mediaTypesToUse = new ArrayList<>();
            for (MediaType requestedType : acceptableTypes) {
                for (MediaType producibleType : producibleTypes) {
                    if (requestedType.isCompatibleWith(producibleType)) {
                        mediaTypesToUse.add(getMostSpecificMediaType(requestedType, producibleType));
                    }
                }
            }
            if (mediaTypesToUse.isEmpty()) {
                if (null != body) {
                    throw new HttpMediaTypeNotAcceptableException(producibleTypes);
                }
                return;
            }

            MediaType.sortBySpecificityAndQuality(mediaTypesToUse);

            for (MediaType mediaType : mediaTypesToUse) {
                if (mediaType.isConcrete()) {
                    selectMediaType = mediaType;
                    break;
                } else if (mediaType.isPresentIn(ALL_APPLICATION_MEDIA_TYPES)) {
                    selectMediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                }
            }
        }


        //至此有了返回值类型returnValueClass和要写进responseBody的content-type类型，然后就是要找到一个支持这两者的HttpMessageConverter，
        if (null != selectMediaType) {
            selectMediaType = selectMediaType.removeQualityValue();
            for (HttpMessageConverter<?> converter : this.messageConverters) {
                GenericHttpMessageConverter genericConverter = converter instanceof GenericHttpMessageConverter
                        ? ((GenericHttpMessageConverter<?>) converter) : null;
                if (null != genericConverter ?
                        ((GenericHttpMessageConverter<?>) converter).canWrite(targetType, valueType, selectMediaType) :
                        converter.canWrite(valueType, selectMediaType)) {
                    if (null != body) {

                        if (null != genericConverter) {
                            genericConverter.write(body, targetType, selectMediaType, outputMessage);
                        } else {
                            ((HttpMessageConverter) converter).write(body, selectMediaType, outputMessage);
                        }
                    }
                }
            }
        }

    }


    /**
     * Return the type of the value to be written to the response. Typically this is
     * a simple check via getClass on the value but if the value is null, then the
     * return type needs to be examined possibly including generic type determination.
     */
    protected Class<?> getReturnValueType(@Nullable Object value, MethodParameter returnType) {
        return null != value ? value.getClass() : returnType.getParameterType();
    }


    private Type getGenericType(MethodParameter returnType) {
        if (HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return ResolvableType.forType(returnType.getGenericParameterType()).getGeneric().getType();
        } else {
            return returnType.getGenericParameterType();
        }
    }

    /**
     * Returns the media types that can be produced. The resulting media types are:
     * <ul>
     *     <li> The producible media types specified in the request mappings, or
     *     <li> Media types of configured converters that can write the specific return value, or
     *     <li> {@link MediaType.ALL}
     * </ul>
     */
    @SuppressWarnings("unchecked")
    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass, @Nullable Type targetType) {
        Set<MediaType> mediaTypes = (Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList<>(mediaTypes);
        }
        List<MediaType> result = new ArrayList<>();
        for (HttpMessageConverter<?> converter : this.messageConverters) {
            if (converter.canWrite(valueClass, null)) {
                result.addAll(converter.getSupportedMediaTypes(valueClass));
            }
        }

        return result.isEmpty() ? Collections.singletonList(MediaType.ALL) : result;
    }

    private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
    }


    /**
     * Return the more specific of the acceptable and the producible media types with
     * the q-value of the former.
     */
    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceTypeToUse = produceType.copyQualityValue(acceptType);
        return MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceTypeToUse) <= 0 ? acceptType : produceTypeToUse;
    }
}
