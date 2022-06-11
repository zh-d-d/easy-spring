package com.zhangdd.example.httpservlet.entity;

/**
 * @author zhangdd on 2022/6/8
 */
public class User {

    private String name;

    private String userId;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
