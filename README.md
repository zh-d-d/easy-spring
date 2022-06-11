# 前言

> 对于后端开发来说Spring是个非常重要的内容。
>
> 它极大的简化了开发流程提高了开发效率，帮助我屏蔽了很多重复性的东西，或者说对于业务开发来说不用关心的东西。
>
> 渐渐的原生的Java web是什么，应该怎么做我都忘记了。
>
> Spring提供了丰富的功能来应对各种场景，所以其源码100%阅读也是一个庞大工程。
>
> 因此决定定义一个目标场景，通过临摹spring-webmvc和spring-web两个模块，来看Spring是如何实现的。
>
> 力争做到
>
> 1. 尽可能少的考虑目标场景外需要的源码，先关心目标场景需要的核心结构
> 2. 熟悉并掌握Spring帮助我实现了Java web开发的哪些内容

# 环境

- JDK 11

- spring-framework 5.3.13
  - spring-core
  - spring-context
  - ....

- Tomcat 8.5.79

# 目标场景

最终期望使用自己简化后的spring-webmvc实现`example-goal`模块的功能。

example-goal功能如下：

使用spring-webmvc实现一个Java web项目。

- 在web.xml声名使用的Servlet；
- 通过spring-mvc-config.xml定义业务接口；
- 调用接口返回Json数据。
- 返回结果如下图

![image-20220607224510676](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220607224510676.png)

# 代码结构

![image-20220607224451752](https://raw.githubusercontent.com/zh-d-d/pic-repository/main/image-20220607224451752.png)

- spring-webmvc和spring-web是要实现的两个模块
- example中定义了两个项目
  - 一个是目标项目example-goal
  - 一个是要依赖自己实现的spring模块达到相同的结果

