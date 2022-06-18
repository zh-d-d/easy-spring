package com.zhangdd.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhangdd on 2022/6/18
 */
@Controller
public class BeanController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/api/beans")
    public List<String> beans() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        return Arrays.asList(beanDefinitionNames);
    }

}