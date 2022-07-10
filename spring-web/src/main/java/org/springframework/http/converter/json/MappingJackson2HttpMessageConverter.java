package org.springframework.http.converter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

/**
 * @author zhangdd on 2022/7/9
 */
public class MappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public MappingJackson2HttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

}
