package org.springframework.web.util;

import org.springframework.util.StringUtils;

import java.nio.charset.Charset;

/**
 * @author zhangdd on 2022/6/19
 */
public abstract class UriUtils {


    public static String decode(String source, String encoding) {
        return StringUtils.uriDecode(source, Charset.forName(encoding));
    }
}
