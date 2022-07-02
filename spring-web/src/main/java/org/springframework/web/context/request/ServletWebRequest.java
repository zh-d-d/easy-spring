package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangdd on 2022/7/2
 */
public class ServletWebRequest extends ServletRequestAttributes implements NativeWebRequest {


    public ServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {

    }
}
