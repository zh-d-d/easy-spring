package org.springframework.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhangdd on 2022/6/19
 */
public class HandlerExecutionChain {

    private static final Log logger= LogFactory.getLog(HandlerExecutionChain.class);

    private final Object handler;

    public HandlerExecutionChain(Object handler) {
        this.handler = handler;
    }


    public Object getHandler() {
        return handler;
    }
}
