package org.springframework.web.servlet;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
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

    public static final Class<?> DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

    private Class<?> contextClass = DEFAULT_CONTEXT_CLASS;

    @Nullable
    private String contextConfigLocation;

    @Nullable
    private WebApplicationContext webApplicationContext;


    private volatile boolean refreshEventReceived;

    private final Object onRefreshMonitor = new Object();

    public Class<?> getContextClass() {
        return contextClass;
    }

    public void setContextClass(Class<?> contextClass) {
        this.contextClass = contextClass;
    }

    @Nullable
    public String getContextConfigLocation() {
        return contextConfigLocation;
    }

    public void setContextConfigLocation(@Nullable String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    /**
     * Callback that receives refresh events from this servlet's
     * WebApplicationContext.
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.refreshEventReceived = true;
        synchronized (this.onRefreshMonitor) {
            onRefresh(event.getApplicationContext());
        }
    }

    /**
     * Template method which can be overridden to add servlet-specific refresh work.
     * Called after successful context refresh.
     */
    protected void onRefresh(ApplicationContext context) {

    }


    @Override
    protected void initServletBean() throws ServletException {
        this.webApplicationContext = initWebApplicationContext();
    }


    protected WebApplicationContext initWebApplicationContext() {
        WebApplicationContext wac = createWebApplicationContext(null);
        return wac;
    }

    protected WebApplicationContext createWebApplicationContext(@Nullable WebApplicationContext parent) {
        return createWebApplicationContext(((ApplicationContext) parent));
    }

    protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
        //获取上下文的class对象，即XmlWebApplicationContext.class
        Class<?> contextClass = getContextClass();
        if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new ApplicationContextException(
                    "Fatal initialization error in servlet with name '" + getServletName() +
                            "': custom WebApplicationContext class [" + contextClass.getName() +
                            "] is not of type ConfigurableWebApplicationContext"
            );
        }

        //构造一个XmlWebApplicationContext对象
        ConfigurableWebApplicationContext wac = (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

        wac.setParent(parent);

        //获取设置的spring配置文件
        String configLocation = getContextConfigLocation();
        if (null != configLocation) {
            wac.setConfigLocation(configLocation);
        }
        configureAndRefreshWebApplicationContext(wac);

        return wac;
    }

    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {

        //注册回调监听
        wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));

        wac.refresh();
    }

    /**
     * ApplicationListener endpoint that receives events from this servlet's
     * WebApplicationContext only, delegating to onApplicationEvent on the
     * FrameworkServlet instance.
     */
    private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            FrameworkServlet.this.onApplicationEvent(event);
        }
    }

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
