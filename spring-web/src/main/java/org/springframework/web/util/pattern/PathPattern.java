package org.springframework.web.util.pattern;

import org.springframework.lang.Nullable;

/**
 * @author zhangdd on 2022/6/23
 */
public class PathPattern implements Comparable<PathPattern>{




    PathPattern(String patternText, PathPatternParser parser, @Nullable PathElement head) {
    }




    @Override
    public int compareTo(PathPattern o) {
        return 0;
    }
}
