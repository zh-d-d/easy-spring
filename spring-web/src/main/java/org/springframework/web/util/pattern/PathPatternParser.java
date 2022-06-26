package org.springframework.web.util.pattern;

/**
 * Parser for URI path patterns producing {@link PathPattern} instance that can
 * then be matched to requests
 * <p>
 * The {@link PathPatternParser} and {@link PathPattern} are specifically
 * designed for use with HTTP URL paths in web application where a large number
 * of URI path patterns, continuously matched against incoming requests,
 * motivates the need for efficient matching.
 *
 * @author zhangdd on 2022/6/23
 */
public class PathPatternParser {

    public PathPattern parse(String pathPattern){
        return new InternalPathPatternParser(this).parse(pathPattern);
    }
}
