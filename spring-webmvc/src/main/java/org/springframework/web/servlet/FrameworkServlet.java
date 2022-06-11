package org.springframework.web.servlet;

import org.springframework.http.HttpMethod;
import org.springframework.web.util.NestedServletException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangdd on 2022/6/11
 */
public abstract class FrameworkServlet extends HttpServletBean {

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpMethod httpMethod = HttpMethod.resolve(req.getMethod());
        if (null == httpMethod || HttpMethod.PATCH == httpMethod) {
            processRequest(req, resp);
        } else {
            super.service(req, resp);
        }
    }


    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected final void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            doService(request, response);
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new NestedServletException("Request processing failed", e);
        }
    }
}
