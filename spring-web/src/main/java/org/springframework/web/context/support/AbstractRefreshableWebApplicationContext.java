package org.springframework.web.context.support;

import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author zhangdd on 2022/6/12
 */
public abstract class AbstractRefreshableWebApplicationContext extends AbstractRefreshableConfigApplicationContext
        implements ConfigurableWebApplicationContext {


    @Override
    public String[] getConfigLocations() {
        return super.getConfigLocations();
    }
}
