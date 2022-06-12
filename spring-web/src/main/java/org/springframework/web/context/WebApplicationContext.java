package org.springframework.web.context;

import org.springframework.context.ApplicationContext;

/**
 * Interface to provide configuration for a web application. This is read-only while
 * the application is running, but may be reloaded if the implementation supports
 * this.
 *
 * @author zhangdd on 2022/6/12
 */
public interface WebApplicationContext extends ApplicationContext {

}
