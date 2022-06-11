package org.springframework.web.servlet;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServlet;

/**
 * @author zhangdd on 2022/6/11
 */
public abstract class HttpServletBean extends HttpServlet {

    protected final Log logger = LogFactory.getLog(getClass());
}
