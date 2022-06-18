package org.springframework.web.servlet.mvc.method;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * @author zhangdd on 2022/6/18
 */
public class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {


    @Nullable
    private final String name;


    public RequestMappingInfo(@Nullable String name) {
        this.name = StringUtils.hasText(name) ? name : null;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public RequestMappingInfo combine(RequestMappingInfo other) {
        return null;
    }


    public static Builder paths(String...paths){
        return new DefaultBuilder(paths);
    }

    /**
     * Defines a builder for creating a RequestMappingInfo.
     */
    public interface Builder {

        Builder paths(String... paths);

        Builder mappingName(String name);

        Builder customCondition(RequestCondition<?> condition);

        RequestMappingInfo build();
    }

    private static class DefaultBuilder implements Builder {

        private String[] paths;

        @Nullable
        private String mappingName;

        @Nullable
        private RequestCondition<?> customCondition;

        public DefaultBuilder(String... paths) {
            this.paths = paths;
        }

        @Override
        public Builder paths(String... paths) {
            this.paths = paths;
            return this;
        }

        @Override
        public Builder mappingName(String name) {
            this.mappingName = name;
            return this;
        }

        @Override
        public Builder customCondition(RequestCondition<?> condition) {
            this.customCondition = condition;
            return this;
        }

        @Override
        public RequestMappingInfo build() {
            return new RequestMappingInfo(this.mappingName);
        }
    }
}
