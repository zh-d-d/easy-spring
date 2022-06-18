package org.springframework.web.context.support;

import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.context.WebApplicationContext;

/**
 * Convenient superclass for application objects running in a {@link WebApplicationContext}.
 * Provides {@code getWebApplicationContext()}, {@code getServletContext()}, and
 * {@code getTempDir()} accessors.
 *
 * @author zhangdd on 2022/6/18
 */
public abstract class WebApplicationObjectSupport extends ApplicationObjectSupport {
}
