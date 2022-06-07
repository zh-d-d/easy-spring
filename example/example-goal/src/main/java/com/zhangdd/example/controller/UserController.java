package com.zhangdd.example.controller;

import com.zhangdd.example.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhangdd on 2022/6/7
 */
@Controller
public class UserController {

    @ResponseBody
    @RequestMapping("/user-info")
    public User user() {
        return buildUser();
    }


    private User buildUser() {
        User user = new User();
        user.setName("小张");
        user.setAddress("杭州");
        return user;
    }
}
