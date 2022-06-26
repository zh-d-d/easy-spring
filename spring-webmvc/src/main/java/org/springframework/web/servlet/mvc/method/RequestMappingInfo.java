package org.springframework.web.servlet.mvc.method;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.util.pattern.PathPatternParser;


import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author zhangdd on 2022/6/18
 */
public class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {

    private static final PathPatternsRequestCondition EMPTY_PATH_PATTERNS = new PathPatternsRequestCondition();

    private static final PatternsRequestCondition EMPTY_PATTERNS = new PatternsRequestCondition();

    @Nullable
    private final String name;

    @Nullable
    private final PathPatternsRequestCondition pathPatternsCondition;

    @Nullable
    private final PatternsRequestCondition patternsCondition;


    public RequestMappingInfo(@Nullable String name,
                              @Nullable PathPatternsRequestCondition pathPatternsCondition, @Nullable PatternsRequestCondition patternsCondition) {
        Assert.isTrue(pathPatternsCondition != null || patternsCondition != null,
                "Neither PathPatterns nor String patterns condition");

        this.name = StringUtils.hasText(name) ? name : null;
        this.pathPatternsCondition = pathPatternsCondition;
        this.patternsCondition = patternsCondition;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public Set<String> getDirectPaths() {
        RequestCondition<?> condition = getActivePatternsCondition();

        return condition instanceof PathPatternsRequestCondition ?
                ((PathPatternsRequestCondition) condition).getDirectPaths() :
                ((PatternsRequestCondition) condition).getDirectPaths();
    }

    @Override
    public RequestMappingInfo combine(RequestMappingInfo other) {
        return null;
    }

    @Override
    public RequestMappingInfo getMatchingCondition(HttpServletRequest request) {

        PathPatternsRequestCondition pathPatterns = null;
        if (null != this.pathPatternsCondition) {
            pathPatterns = this.pathPatternsCondition.getMatchingCondition(request);
            if (null == pathPatterns) {
                return null;
            }
        }

        PatternsRequestCondition patterns = null;
        if (null != this.patternsCondition) {
            patterns = this.patternsCondition.getMatchingCondition(request);
            if (null == patterns) {
                return null;
            }
        }
        return new RequestMappingInfo(this.name, pathPatterns, patterns);
    }

    public static Builder paths(String... paths) {
        return new DefaultBuilder(paths);
    }

    @SuppressWarnings("unchecked")
    public <T> RequestCondition<T> getActivePatternsCondition() {
        if (null != this.pathPatternsCondition) {
            return (RequestCondition<T>) this.pathPatternsCondition;
        } else if (null != this.patternsCondition) {
            return (RequestCondition<T>) this.patternsCondition;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Defines a builder for creating a RequestMappingInfo.
     */
    public interface Builder {

        Builder paths(String... paths);

        Builder mappingName(String name);

        Builder customCondition(RequestCondition<?> condition);

        Builder options(BuilderConfiguration options);

        RequestMappingInfo build();
    }

    private static class DefaultBuilder implements Builder {

        private String[] paths;

        @Nullable
        private String mappingName;

        @Nullable
        private RequestCondition<?> customCondition;

        private BuilderConfiguration options = new BuilderConfiguration();

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
        public Builder options(BuilderConfiguration options) {
            this.options = options;
            return this;
        }

        @Override
        public RequestMappingInfo build() {

            PathPatternsRequestCondition pathPatterns = null;
            PatternsRequestCondition patterns = null;

            if (null != this.options.patternParser) {
                pathPatterns = (ObjectUtils.isEmpty(this.paths)) ?
                        EMPTY_PATH_PATTERNS : new PathPatternsRequestCondition(this.options.patternParser, this.paths);
            } else {
                patterns = (ObjectUtils.isEmpty(this.paths)) ?
                        EMPTY_PATTERNS :
                        new PatternsRequestCondition(this.paths, null, this.options.getPathMatcher(), false, false, null);
            }

            return new RequestMappingInfo(this.mappingName, pathPatterns, patterns);
        }
    }

    /**
     * Container for configuration operations used for request mapping purpose. Such
     * configuration is required to create RequestMappingInfo instance but is
     * typically used across all RequestMappingInfo instances.
     */
    public static class BuilderConfiguration {

        @Nullable
        private PathPatternParser patternParser;

        @Nullable
        private PathMatcher pathMatcher;


        public void setPatternParser(@Nullable PathPatternParser patternParser) {
            this.patternParser = patternParser;
        }

        @Nullable
        public PathPatternParser getPathPatternParser() {
            return this.patternParser;
        }


        public void setPathMatcher(@Nullable PathMatcher pathMatcher) {
            this.pathMatcher = pathMatcher;
        }

        @Nullable
        public PathMatcher getPathMatcher() {
            return pathMatcher;
        }
    }
}
