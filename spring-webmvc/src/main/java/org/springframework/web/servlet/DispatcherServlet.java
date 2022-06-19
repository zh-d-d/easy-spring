package org.springframework.web.servlet;

import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangdd on 2022/6/11
 */
public class DispatcherServlet extends FrameworkServlet {


    @Override
    protected void onRefresh(ApplicationContext context) {
        initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context){

    }


    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        doDispatch(request, response);
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("接收到请求:path: " + request.getRequestURI());
    }
}
