package org.springframework.web.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.io.IOException;

/**
 * @author zhangdd on 2022/6/12
 */
public class XmlWebApplicationContext extends AbstractRefreshableWebApplicationContext {

    /**
     * Loads the bean definitions via an XmlBeanDefinitionReader.
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        //Create a new XmlBeanDefinitionReader for this BeanFactory
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        //Config the bean definition reader with this context's
        //resource loading environment
        beanDefinitionReader.setEnvironment(getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        //Allow a subclass to provide custom initialization of the reader,
        //then proceed with actually loading the bean definitions.


        loadBeanDefinitions(beanDefinitionReader);
    }

    /**
     * Load the bean definitions with the given XmlBeanDefinitionReader.
     * The lifecycle of the bean factory is handled by the refreshBeanFactory method;
     * therefore this method is just supposed to load and/or register bean
     * definitions.
     */
    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) {
        String[] configLocations = getConfigLocations();
        if (null != configLocations) {
            for (String configLocation : configLocations) {
                reader.loadBeanDefinitions(configLocation);
            }
        }
    }
}
