package org.springframework.web.servlet.mvc.condition;

import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author zhangdd on 2022/6/25
 */
public class PathPatternsRequestCondition extends AbstractRequestCondition<PathPatternsRequestCondition> {


    private static final SortedSet<PathPattern> EMPTY_PATH_PATTERN =
            new TreeSet<>(Collections.singleton(new PathPatternParser().parse("")));

    private static final Set<String> EMPTY_PATH = Collections.singleton("");

    private final SortedSet<PathPattern> patterns;

    public PathPatternsRequestCondition() {
        this(EMPTY_PATH_PATTERN);
    }


    public PathPatternsRequestCondition(PathPatternParser parser, String... patterns) {
        this(parse(parser, patterns));
    }

    private PathPatternsRequestCondition(SortedSet<PathPattern> patterns) {
        this.patterns = patterns;
    }

    private static SortedSet<PathPattern> parse(PathPatternParser parser, String... patterns) {
        if (patterns.length == 0 || (patterns.length == 1 && !StringUtils.hasText(patterns[0]))) {
            return EMPTY_PATH_PATTERN;
        }
        SortedSet<PathPattern> result = new TreeSet<>();
        for (String path : patterns) {
            if (StringUtils.hasText(path) && !path.startsWith("/")) {
                path = "/" + path;
            }
            result.add(parser.parse(path));
        }
        return result;
    }

    @Override
    public PathPatternsRequestCondition combine(PathPatternsRequestCondition other) {
        return null;
    }

    @Override
    public PathPatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        return null;
    }

    public boolean isEmptyPathMapping() {
        return this.patterns == EMPTY_PATH_PATTERN;
    }

    public Set<String> getDirectPaths() {
        return null;
    }
}
