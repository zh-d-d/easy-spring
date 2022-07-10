package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author zhangdd on 2022/7/9
 */
public abstract class AbstractMessageConverterMethodArgumentResolver {


    protected final List<HttpMessageConverter<?>> messageConverters;

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters,
                                                          @Nullable List<Object> requestResponseBodyAdvice) {
        Assert.notEmpty(converters, "'messageConverters' must not be empty");
        this.messageConverters = converters;
    }
}
