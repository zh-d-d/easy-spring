package org.springframework.http;

import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangdd on 2022/6/11
 */
public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, HttpMethod> mappings = Arrays.stream(values())
            .collect(Collectors.toMap(Enum::name, Function.identity()));


    @Nullable
    public static HttpMethod resolve(@Nullable String method) {
        return method != null ? mappings.get(method) : null;
    }

    public boolean matches(String method) {
        return name().equals(method);
    }
}
