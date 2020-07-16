

# 两种引入 public 插件的方式



```groovy
plugins {
   id 'org.hidetake.ssh' version '1.1.2'
}
```

```java
apply plugin: 'someplugin1'
apply plugin: 'maven'
```

From [stackoverflow](https://stackoverflow.com/questions/32352816/what-the-difference-in-applying-gradle-plugin)

> The `plugins` block is the newer method of applying plugins, and they must be available in the [Gradle plugin repository](http://plugins.gradle.org/). The `apply` approach is the older, yet more flexible method of adding a plugin to your build.
>
> The new `plugins` method does not work in multi-project configurations (`subprojects`, `allprojects`), but will work on the build configuration for each child project.

> I would think that as functionality progresses, the `plugins` configuration method will overtake the older approach, but at this point both can be and are used concurrently.



# 构建原理

# Lifecycles

# Android Gradle Puglin 

# How to create Gradle Plugin

**Best Article**：

[The Complete Gradle Plugin Tutorial](https://dzone.com/articles/the-complete-custom-gradle-plugin-building-tutoria)

[How to Test Gradle Plugins](https://dzone.com/articles/functional-tests-gradle-plugin)



[Gradle 5.6.4 Doc](https://docs.gradle.org/5.6.4/userguide/custom_plugins.html#sec:custom_plugins_standalone_project) (到20年五月底 Android 编译是基于 5.6.4 的，为了兼容采用同样版本)

**PS：** 中文资料太老了，而且大多雷同错误很多。官方文档和官方 sample 有太片面，完全不知所云。



