package org.springframework.web.util.pattern;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Parser for URI template patterns. It breaks the path pattern into a number of
 * {@link PathElement} in a linked list. Instances are reusable but are not thread-safe.
 *
 * @author zhangdd on 2022/6/25
 */
public class InternalPathPatternParser {

    private final PathPatternParser parser;

    // The input data for parsing
    private char[] pathPatternData = new char[0];

    // The length of the input data
    private int pathPatternLength;

    // Current parsing position
    int pos;

    // How many ? characters in a particular path element
    private int singleCharWildcardCount;

    // Is the path pattern using * characters in a particular path element
    private boolean wildcard = false;

    // Is the construct {*...} being used in a particular path element
    private boolean isCaptureTheRestVariable = false;

    // Has the parser entered a {...} variable capture block in a particular
    // path element
    private boolean insideVariableCapture = false;

    // How many variable captures are occurring in a particular path element
    private int variableCaptureCount = 0;

    // Start of the most recent path element in a particular path element
    private int pathElementStart;

    // Start of the most recent variable capture in a particular path element
    private int variableCaptureStart;

    // Variables captures in this path pattern
    @Nullable
    private List<String> capturedVariableNames;

    // The head of the path element chain currently being built
    @Nullable
    private PathElement headPE;

    // The most recently constructed path element in the chain
    @Nullable
    private PathElement currentPE;


    /**
     * Package private constructor for use in {@link PathPatternParser#parse}.
     *
     * @param parentParser reference back to the stateless, public parser
     */
    InternalPathPatternParser(PathPatternParser parentParser) {
        this.parser = parentParser;
    }


    /**
     * Package private delegate for {@link PathPatternParser#parse(String)}.
     */
    public PathPattern parse(String pathPattern) throws PatternParseException {
        Assert.notNull(pathPattern, "Path pattern must not be null");

        this.pathPatternData = pathPattern.toCharArray();
        this.pathPatternLength = this.pathPatternData.length;
        this.headPE = null;
        this.currentPE = null;
        this.capturedVariableNames = null;
        this.pathElementStart = -1;
        this.pos = 0;


        return new PathPattern(pathPattern, this.parser, this.headPE);
    }

}
