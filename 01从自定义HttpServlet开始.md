# 开篇

工作中用了那么久Spring Boot或者Spring mvc，你还知道如何使用HttpServlet来实现example-goal的功能吗？

本篇属于开张篇，先通过自定义一个Servlet来实现example-goal的功能。并由此引出一系列问题掀开后序篇章。

# 代码实现

## 通过继承HttpServlet实现

1. 创建一个Servlet用来处理获取用户信息

   创建UserServlet继承HttpServlet，设置ContentType、构造返回数据。代码如下：

```java
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
```

2. 在web.xml文件中生命创建的Servlet，同时声名servlet-mapping

```xml
<web-app>

  <servlet>
    <servlet-name>UserServlet</servlet-name>
    <servlet-class>com.zhangdd.example.httpservlet.servlet.UserServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>UserServlet</servlet-name>
    <url-pattern>/user-info</url-pattern>
  </servlet-mapping>
  
</web-app>

```

3. 配置tomcat并启动服务访问配置的URL结果如下图所示

![image-20220608220107366](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220608220107366.png)

## 对比example-goal的实现

### example-goal的web.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">


    <!--    设置编码过滤器-->
    <filter>
        <filter-name>CharacterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceResponseEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>CharacterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!--    设置put delete patch请求方法处理过滤器-->
    <filter>
        <filter-name>HiddenHttpMethodFilter</filter-name>
        <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>HiddenHttpMethodFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!--    设置Springmvc前端处理器-->
    <servlet>
        <servlet-name>DispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring-mvc-config.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>DispatcherServlet</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>

</web-app>


```

看到了相同的配置项：

- servlet
  - 配置了初始化参数设置contextConfigLocation的值为classpath路径下的spring-mvc-config.xml文件
- servlet-mapping
  - 匹配所有的路径

### example-goal的spring-mvc-config.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"

       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/mvc
       https://www.springframework.org/schema/mvc/spring-mvc.xsd
       http://www.springframework.org/schema/context
       https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="com.zhangdd.example.controller"/>

    <mvc:annotation-driven/>
</beans>
```

这里只是配置了要扫描的包和开启mvc注解支持，好像好看不出什么。

### example-goal的业务实现

```java
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

```



# 提出问题

从可见层面上对比example-httpservlet和example-goal提出以下疑问？

1. DispatcherServlet配置的匹配URL是所有路径，那是怎么映射交给@RequestMapping("/user-info")标识的业务方法的？
2. 问题1是一个后置的匹配操作，即服务启动后等待用户请求，接收到用户的请求后才能进行匹配查找。说明有一个注册过程，那是什么时候注册的，怎么注册的，匹配的过程是怎么匹配的？
3. 业务方法只是返回了一个Java对象，用户接收到的是个Json数据，这个是在哪个阶段如何处理的？

这三个问题反应的就是如下两张图

spring-webmvc启动注册过程：

![image-20220608224632316](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220608224632316.png)

spring-webmvc接收请求流程图

![image-20220608230255250](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220608230255250.png)

