package org.springframework.web.servlet;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangdd on 2022/6/11
 */
public abstract class HttpServletBean extends HttpServlet {

    protected final Log logger = LogFactory.getLog(getClass());

    private final Set<String> requiredProperties = new HashSet<>(4);


    @Override
    public void init() throws ServletException {
        //1.获取Servlet设置的初始化参数
        PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
        if (!pvs.isEmpty()) {

            try {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
                //2.这里将获取到的参数交给bw去设置到对应的set方法
                bw.setPropertyValues(pvs, true);
            } catch (BeansException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", e);
                }
                throw e;
            }
        }

        //3.在初始化参数处理完成后，调用模版方法让子类进行初始化
        initServletBean();
    }

    /**
     * Subclasses may override this to perform initialization. All bean
     * properties of this servlet will have been set before this method is invoked.
     */
    protected void initServletBean() throws ServletException {

    }

    /**
     * PropertyValues implementation created from ServletConfig init parameters.
     */
    private static class ServletConfigPropertyValues extends MutablePropertyValues {

        public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties) throws ServletException {

            HashSet<String> missingProps = !CollectionUtils.isEmpty(requiredProperties)
                    ? new HashSet<>(requiredProperties) : null;

            Enumeration<String> parameterNames = config.getInitParameterNames();
            while (parameterNames.hasMoreElements()) {
                String property = parameterNames.nextElement();
                String value = config.getInitParameter(property);
                addPropertyValue(new PropertyValue(property, value));
                if (null != missingProps) {
                    missingProps.remove(property);
                }
            }

            //fail if we are still missing properties
            if (!CollectionUtils.isEmpty(missingProps)) {
                throw new ServletException(
                        "Initialization from ServletConfig for servlet '" + config.getServletName() +
                                "' failed; the following required properties were missing: " +
                                StringUtils.collectionToDelimitedString(missingProps, ", "));
            }

        }
    }
}
