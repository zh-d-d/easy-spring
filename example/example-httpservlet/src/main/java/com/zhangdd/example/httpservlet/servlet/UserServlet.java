package com.zhangdd.example.httpservlet.servlet;

import cn.hutool.json.JSONUtil;
import com.zhangdd.example.httpservlet.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhangdd on 2022/6/8
 */
@Slf4j
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        User user = buildUser();

        bufferedWriter.write(JSONUtil.toJsonStr(user));
        bufferedWriter.flush();
        writer.close();
        bufferedWriter.close();
    }

    private User buildUser() {
        User user = new User();
        user.setName("小张");
        user.setAddress("杭州");
        return user;
    }
}
