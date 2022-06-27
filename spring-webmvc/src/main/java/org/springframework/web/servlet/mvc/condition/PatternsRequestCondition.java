package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 使用 AntPathMatcher 进行字符串的匹配
 *
 * @author zhangdd on 2022/6/22
 */
public class PatternsRequestCondition extends AbstractRequestCondition<PatternsRequestCondition> {

    private final static Set<String> EMPTY_PATH_PATTERN = Collections.singleton("");


    private final Set<String> patterns;

    private final PathMatcher pathMatcher;

    private final boolean useSuffixPatternMatch;

    private final boolean useTrailingSlashMatch;

    private final List<String> fileExtensions = new ArrayList<>();

    public PatternsRequestCondition(String... patterns) {
        this(patterns, true, null);
    }

    public PatternsRequestCondition(String[] patterns, boolean useTrailingSlashMatch, @Nullable PathMatcher pathMatcher) {
        this(patterns, null, pathMatcher, useTrailingSlashMatch);
    }

    public PatternsRequestCondition(String[] patterns, @Nullable UrlPathHelper urlPathHelper, @Nullable PathMatcher pathMatcher, boolean useTrailingSlashMatch) {
        this(patterns, urlPathHelper, pathMatcher, false, useTrailingSlashMatch);
    }

    public PatternsRequestCondition(String[] pattern, @Nullable UrlPathHelper urlPathHelper,
                                    @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch) {
        this(pattern, urlPathHelper, pathMatcher, useSuffixPatternMatch, useTrailingSlashMatch, null);
    }

    public PatternsRequestCondition(String[] pattern, @Nullable UrlPathHelper urlPathHelper,
                                    @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch,
                                    boolean useTrailingSlashMatch, @Nullable List<String> fileExtensions) {
        this.patterns = initPatterns(pattern);
        this.pathMatcher = pathMatcher != null ? pathMatcher : new AntPathMatcher();
        this.useSuffixPatternMatch = useSuffixPatternMatch;
        this.useTrailingSlashMatch = useTrailingSlashMatch;


    }


    private PatternsRequestCondition(Set<String> patterns, PatternsRequestCondition other) {
        this.patterns = patterns;
        this.pathMatcher = other.pathMatcher;
        this.useSuffixPatternMatch = other.useSuffixPatternMatch;
        this.useTrailingSlashMatch = other.useTrailingSlashMatch;
        this.fileExtensions.addAll(other.fileExtensions);
    }


    private static Set<String> initPatterns(String[] patterns) {
        if (!hasPattern(patterns)) {
            return EMPTY_PATH_PATTERN;
        }
        Set<String> result = new LinkedHashSet<>(patterns.length);
        for (String pattern : patterns) {
            if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
                pattern = "/" + pattern;
            }
            result.add(pattern);
        }

        return result;
    }

    private static boolean hasPattern(String[] patterns) {
        if (!ObjectUtils.isEmpty(patterns)) {
            for (String pattern : patterns) {
                if (StringUtils.hasText(pattern)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Whether the condition is the "" (empty path) mapping.
     */
    public boolean isEmptyPathMapping() {
        return this.patterns == EMPTY_PATH_PATTERN;
    }

    public Set<String> getDirectPaths() {
        if (isEmptyPathMapping()) {
            return EMPTY_PATH_PATTERN;
        }
        Set<String> result = Collections.emptySet();
        for (String pattern : this.patterns) {
            if (!this.pathMatcher.isPattern(pattern)) {
                result = (result.isEmpty() ? new HashSet<>(1) : result);
                result.add(pattern);
            }
        }

        return result;
    }

    @Override
    public PatternsRequestCondition combine(PatternsRequestCondition other) {
        return null;
    }

    @Override
    public PatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        String lookupPath = UrlPathHelper.getResolvedLookupPath(request);
        List<String> matches = getMatchingPatterns(lookupPath);
        return !matches.isEmpty() ? new PatternsRequestCondition(new LinkedHashSet<>(matches), this) : null;
    }

    /**
     * Find the patterns matching the given lookup path. Invoking this method should
     * yield results equivalent to those of calling {@link #getMatchingCondition}.
     * This method is provided as an alternative to be used if no request is available
     * (e.g. introspection, tooling, etc).
     */
    public List<String> getMatchingPatterns(String lookupPath) {
        List<String> matches = null;
        for (String pattern : this.patterns) {
            String match = getMatchingPattern(pattern, lookupPath);
            if (null != match) {
                matches = (null != matches ? matches : new ArrayList<>());
                matches.add(match);
            }
        }
        if (null == matches) {
            return Collections.emptyList();
        }
        if (matches.size() > 1) {
            this.pathMatcher.getPatternComparator(lookupPath);
        }
        return matches;
    }

    @Nullable
    private String getMatchingPattern(String pattern, String lookupPath) {
        if (pattern.equals(lookupPath)) {
            return pattern;
        }
        if (this.useSuffixPatternMatch) {
            if (!this.fileExtensions.isEmpty() && lookupPath.contains(".")) {
                for (String extension : this.fileExtensions) {
                    if (this.pathMatcher.match(pattern + extension, lookupPath)) {
                        return pattern + extension;
                    }
                }
            } else {
                boolean hasSuffix = pattern.contains(".");
                if (!hasSuffix && this.pathMatcher.match(pattern + ".*", lookupPath)) {
                    return pattern + ".*";
                }
            }
        }
        if (this.pathMatcher.match(pattern, lookupPath)) {
            return pattern;
        }
        if (this.useTrailingSlashMatch) {
            if (!pattern.endsWith("/") && this.pathMatcher.match(pattern + "/", lookupPath)) {
                return pattern + "/";
            }
        }
        return null;
    }
}
