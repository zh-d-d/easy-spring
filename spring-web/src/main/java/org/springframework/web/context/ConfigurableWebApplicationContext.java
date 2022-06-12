package org.springframework.web.context;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

/**
 * @author zhangdd on 2022/6/12
 */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {


    void setConfigLocation(String configuration);

    void setConfigLocations(String... configurations);

    @Nullable
    String[] getConfigLocations();
}
