# 涉及内容预览

- Servlet的生命周期
- DispatcherServlet的类结构体系
- 让DispatcherServlet来处理所有的请求

# Servlet的生命周期

在example-goal项目的web.xml配置中就配置了一个DispatcherServlet，DispatcherServlet说白了就是spring实现的一个Servlet，在一定程度上和我们自定义的UserServlet没有什么区别。

既然也是一个Servlet，那就有必要了解下Servlet的生命周期。

Servlet接口定义了如下五个方法

- init(ServletConfig config)
- service(ServletRequest req, ServletResponse res)
- destroy()
- getServletConfig()
- getServletInfo()

## init方法

在Servlet实例化之后，Servlet容器会调用init()方法来初始化该对象，主要是为了让Servlet对象在处理用户请求前可以完成一些初始化的工作，例如：建立数据库的连接，获取配置信息等。

对于每一个Servlet实例，init()方法只能被调用一次。init()方法有一个类型为ServletConfig的参数，Servlet容器通过这个参数向Servlet传递配置信息。Servlet使用ServletConfig对象从web应用程序的配置信息中获取以key-value形式提供的初始化参数。

另外在Servlet中，还可以通过ServletConfig对象获取描述Servlet运行环境的ServletContext对象，使用该对象，Servlet可以和Servlet容器进行通信。

## service方法

容器调用service方法来处理客户端的请求。需要注意的是在service方法被容器调用之前，必需确保init方法正确完成。

容器会构造一个表示客户端请求信息的请求对象（类型为ServletRequest）和一个用于对客户端进行响应的响应对象（类型为ServletResponse）作为参数传递给service方法。在service方法中，Servlet对象通过ServletRequest对象得到客户端的相关信息和请求信息；在对请求进行处理后，调用ServletResponse对象的方法设置响应信息。

## destroy方法

当容器检测到一个Servlet对象应该从服务中被移除的时候，容器会调用该对象的destroy方法。以便让Servlet对象可以释放它所使用的资源、保存数据到持久存储设备中，例如：将内存中的数据保存到数据库中、关闭数据库的连接等。

当需要释放内存或者容器关闭时，容器就会调用Servlet对象的destroy方法。在Servlet容器调用destroy方法前，如果还有其他的线程正在service方法中执行，容器会等待这些线程执行完毕或者等待服务器设置的超时时间到达。

一旦Servlet对象的destroy方法被调用，容器不会再把其他的请求发送给该对象。如果需求该Servlet再次为客户端服务，容器将会重新产生一个Servlet对象来处理客户端的请求。在destroy方法调用之后，容器会释放这个Servlet对象，在随后的时间内，该对象会被Java的垃圾收集器回收。

## getServletConfig方法

该方法返回容器调用init方法时传递给Servlet对象的ServletConfig对象，ServletConfig对象包含了Servlet的初始化参数。

## getServletInfo方法

返回一个String类型的字符串。包括了Servlet的信息，例如：作者、版本、版权。

# DispatcherServlet的类结构体系

![image-20220609232155504](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220609232155504.png)

正如Servlet生命周期所描述，当http请求到达HttpServlet的service方法后：

1. HttpServlet的service方法会将`ServletRequest`、`ServletResponse`转成`HttpServletRequest`和`HttpServletResponse`，同时调用内部的方法交给`service(HttpServletRequest request, HttpServletResponse response)`处理
2. FrameworkServlet类重写了HttpServlet的`service(HttpServletRequest request, HttpServletResponse response)`方法所以http会到这里。起始FrameworkServlet不仅仅重写了这一个方法，还有如下方法doGet、doPost、doPut等。另外在service方法里做了对PATCH请求方式的支持。但最终都会交给`processRequest(HttpServletRequest request, HttpServletResponse response)`方法
3. 在`processRequest(HttpServletRequest request, HttpServletResponse response)`方法中，比较重要的就是调用模版方法`doService(HttpServletRequest request, HttpServletResponse response)`，该方法是个抽象方法由DispatcherServlet实现

# 实现DispatcherServlet处理接收所有的请求

了解了一个http请求从HttpServlet到DispatchServlet的流程，那代码实现也就简单了。那这个主干流程是如何发现的呢？答案就是debug。在UserController的业务方法内进行断点，调用暴露的接口触发断点，这个时候查看当前现场的调用栈情况这个流程也就梳理出来了。

![image-20220611094541892](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220611094541892.png)



spring实现该流程过程中每个方法都做了较多的处理，为了简化我们只对主干方法进行实现。主要代码结构如下

![image-20220611104202895](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220611104202895.png)

```java
public abstract class FrameworkServlet extends HttpServletBean {

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpMethod httpMethod = HttpMethod.resolve(req.getMethod());
        if (null == httpMethod || HttpMethod.PATCH == httpMethod) {
            processRequest(req, resp);
        } else {
            super.service(req, resp);
        }
    }


    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected final void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            doService(request, response);
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new NestedServletException("Request processing failed", e);
        }
    }
}
```

FrameworkServlet重写了HttpServlet的service、doGet、doPost、doPut、doDelete等方法，并都交给processRequest方法进行处理，在processRequest方法中调用自己定义的抽象模版方法doService。

DispatcherServlet继承FrameworkServlet并实现了模仿方法doService

```java
public class DispatcherServlet extends FrameworkServlet {

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        doDispatch(request, response);
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("接收到请求:path: " + request.getRequestURI());
    }
}

```

# 测试

在example-easy-spring项目中使用上面实现的DispatcherServlet来接收所有的请求。

example-easy-spring结构如下

![image-20220611104952454](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220611104952454.png)

主要就是web.xml中声名一个Servlet即我们自己实现的DispatchServlet，同时生命该DispatcherServlet匹配所有的url。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">


    <!--    设置Springmvc前端处理器-->
    <servlet>
        <servlet-name>DispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>DispatcherServlet</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>

</web-app>

```

启动该项目调用该服务暴露的端口，可以看到输出的日志。

![image-20220611105403652](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220611105403652.png)

```tex
11-Jun-2022 10:52:52.982 INFO [http-nio-8080-exec-1] org.springframework.web.servlet.DispatcherServlet.doDispatch 接收到请求:path: /
11-Jun-2022 10:52:52.986 INFO [http-nio-8080-exec-1] org.springframework.web.servlet.DispatcherServlet.doDispatch 接收到请求:path: /
11-Jun-2022 10:52:53.063 INFO [http-nio-8080-exec-2] org.springframework.web.servlet.DispatcherServlet.doDispatch 接收到请求:path: /
11-Jun-2022 10:53:13.607 INFO [http-nio-8080-exec-5] org.springframework.web.servlet.DispatcherServlet.doDispatch 接收到请求:path: /hello
```

