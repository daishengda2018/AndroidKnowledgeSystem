# 概述

测试驱动开发 Test-Driven Development。工作流：Failing Test -> Passing Test -> Refactor -> Failing Test -> …

测试层级。Small Tests（Unit Tests）70% -> Medium Tests 20% -> Large Tests 10%。

Small Tests或单元测试

- Local Unit Tests。最主要的单元测试。运行于本地JVM，源码位于*[module-name]/src/test/java/*；如果测试单元引用了Android层框架或三方依赖代码，可使用Robolectric或Mockito等来模拟。依赖库包括JUnit 4、Robolectric、Mockito。
- Instrumented Unit Tests。运行于模拟器或真机，源码位于*[module-name]/src/androidTest/java/*。依赖库包括AndroidX test runner和rules、Hamcrest、Espresso、UI Automator。

书籍-软件测试

- 《软件测试的艺术》
- 《软件测试自动化》
- 《测试驱动开发的艺术》
- 《单元测试的艺术》
- 《有效的单元测试 》
- 《腾讯Android自动化测试实战》
- 《 JUnit实战》

编写可测试代码

- [改善代码可测性的若干技巧](http://www.importnew.com/27767.html)
- [可测试性代码的重点](http://www.ituring.com.cn/book/miniarticle/133207)
- [如何编写具有可测试性的代码](https://www.cnblogs.com/wenpeng/p/8266472.html)

------

# 入门

简单入门

- [Android 单元测试只看这一篇就够了](https://juejin.im/post/5b57e3fbf265da0f47352618#heading-17)
- [一文全面了解Android单元测试](https://juejin.im/post/5b43817ce51d45198e720f40)
- [官方文档：在 Android 平台上测试应用 ](https://developer.android.com/training/testing)
- [官方文档：Android 测试应用](https://developer.android.com/studio/test?hl=zh-cn)
- [[Android单元测试研究与实践](https://tech.meituan.com/2015/12/24/android-unit-test.html)]

上手使用-某个人博客系列文章

- [Android单元测试: 首先，从是什么开始](http://chriszou.com/2016/04/13/android-unit-testing-start-from-what.html)
- [Android单元测试（二）：再来谈谈为什么](http://chriszou.com/2016/04/16/android-unit-testing-about-why.html)
- [Android单元测试(三)：JUnit单元测试框架的使用](http://chriszou.com/2016/04/18/android-unit-testing-junit.html)
- [Android单元测试（四）：Mock以及Mockito的使用](http://chriszou.com/2016/04/29/android-unit-testing-mockito.html)
- [Android单元测试（五）：依赖注入，将mock方便的用起来](http://chriszou.com/2016/05/06/android-unit-testing-di.html)
- [Android单元测试（六）：使用dagger2来做依赖注入，以及在单元测试中的应用](http://chriszou.com/2016/05/10/android-unit-testing-di-dagger.html)
- [Android单元测试（七）：Robolectric，在JVM上调用安卓的类](http://chriszou.com/2016/06/05/robolectric-android-on-jvm.html)
- [安卓单元测试(八)：Junit Rule的使用](http://chriszou.com/2016/07/09/junit-rule.html)
- [安卓单元测试（九）：使用Mockito Annotation快速创建Mock](http://chriszou.com/2016/07/16/mockito-annotation.html)
- [Android单元测试(十)：DaggerMock：The Power of Dagger2, The Ease of Mockito](http://chriszou.com/2016/07/24/android-unit-testing-daggermock.html)
- [安卓单元测试(十一)：异步代码怎么测试](http://chriszou.com/2016/08/06/android-unit-testing-async.html)

博客资料2：

[Android单元测试](https://weilu.blog.csdn.net/category_9270906.html)

其他资料-Kotlin写 Android 单元测试

- [系列文章](https://www.jianshu.com/p/fdf53b8bc6ed)
- mock final class `testImplementation 'org.mockito:mockito-inline:2.13.0'`

------

# 框架

Junit

Mockito

- [Unit tests with Mockito - Tutorial](https://www.vogella.com/tutorials/Mockito/article.html)

PowerMockito

Robolectric

- [官网](http://robolectric.org/)
- [Robolectric使用教程-3.x.x](http://blog.hanschen.site/2016/12/10/robolectric.html)
- [在library里运行测试](https://github.com/robolectric/deckard/issues/70)

Robolectric4.0完全兼容AndroidX Test库，尽量使用AndroidX API。

Dagger

异步代码测试

# 实践

第一阶段只使用Junit和基础mockito即可，只写Java代码测试以及MVP框架中的p层测试，原因如下：

- mock框架太多。mock框架太多，开始阶段只需要熟练使用使用最广泛的版本mockito即可。
- robolectric坑太多。每个版本的API改变较大；4.0之后与AndroidX兼容，但其实很多项目都没有使用AndroidX。
- 仪器测试太耗时。

```
// junit
testImplementation 'junit:junit:4.12'

// 基础mockito
testImplementation 'org.mockito:mockito-core:2.8.47'
testImplementation 'org.mockito:mockito-inline:2.8.47'

// dagger
implementation 'com.google.dagger:dagger:2.21'
kapt 'com.google.dagger:dagger-compiler:2.21'
```

第二阶段可以增加仪器测试

第三阶段，待AndroidX使用比较广泛后，可增加Robolectric。

------

# 参考

- [googlesamples/**android-testing**](https://github.com/googlesamples/android-testing)
- [Test-Driven Development on Android with the Android Testing Support Library (Google I/O ‘17)](https://www.youtube.com/watch?v=pK7W5npkhho&start=111)
- [googlesamples/android-sunflower](https://github.com/googlesamples/android-sunflower)
- [Android Testing Codelab](https://codelabs.developers.google.com/codelabs/android-testing/index.html#0)。教程不错，可惜依赖库不是最新的，也没有用Robolectric。
- [Adroid单元测试 系列文章](http://chriszou.com/2016/04/16/android-unit-testing-about-why.html)

实践。优化既有项目，测试工具类（单元测试或小型测试）、测试页面逻辑（简单UI测试或中型测试，可先优化MVP结构）、测试页面间逻辑（集成UI测试或大型测试）。

# 学习资料

[测试基础知识](https://developer.android.com/training/testing/fundamentals?hl=zh-cn)





