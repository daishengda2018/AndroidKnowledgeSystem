

[toc]

# 概述

启动一个 Activity 的方法很简单，在显式调用的情形下，只需要通过如下代码：

```Java
Intent intent = new Intent(this, TestActivity.class);
startActivity(this);
```

但是一个 Activity 的启动过程是很复杂的。

## 问题：为啥要写的这么复杂呢？为什么要绕一大圈？

因为要进程间交互。

## instrumentation 到底是个什么东西。







# 总结1：

通过对在应用里面启动新的Activity的过程进行源码跟踪，我们发现这里面主要涉及到几个类：Activity、ActivityThread、ApplicationThread、ActivityManagerService。

（1）Activity是我们发起启动新的Activity的入口，也是最后完成Activity操作结束的地方。我们通过Activity的startActivity发起启动新Activity的请求，最后通过Activity的attach方法完成Context的绑定和窗口Window的创建和绑定。

（2）ActivityThread是启动Activity的处理者，也是一个中间人的角色，通过调用其他几个类完成启动Activity的任务。它首先通过Binder机制调用ActivityManagerService完成Activity相关的系统级的操作，比如任务栈，暂停其他Activity等，然后通过内部的Binder类ApplicationThread接收ActivityManagerService的进程间请求，将启动的操作重新转回到当前应用进程。接着通过调用Instrumentation和LoadApk的相关方法完成加载Activity类和Application的任务。最后调用Activity的attach方法完成一系列的绑定操作。

（3）ApplicationThread是一个Binder类，用于和ActivityManagerService的进程间通信。

（4）ActivityManagerService是系统的一个服务，用于管理Activity的状态和相关信息，比如任务栈等。

如果是直接点击桌面的应用图标启动应用呢？其实这个过程和启动Activity类似，都是需要启动一个Activity。不过启动应用启动的是应用的入口Activity，同时是从桌面应用启动另一个应用程序的Activity，所以过程肯定会多一些步骤，比如要找到应用中的入口Activity，创建新的应用程序进程，要创建任务栈，要移除桌面的焦点等。等这些准备工作都好了以后，后面就相当于是启动一个Activity的过程了。有兴趣的童鞋可以研究一下


# 总结2：

[startActivity启动过程分析](http://gityuan.com/2016/03/12/start-activity/)本文详细startActivity的整个启动流程，

- 流程[**2.1 ~2.4**]:运行在调用者所在进程，比如从桌面启动Activity，则调用者所在进程为launcher进程，launcher进程利用ActivityManagerProxy作为Binder Client，进入system_server进程(AMS相应的Server端)。
- 流程[2.5 ~2.18]:运行在system_server系统进程，整个过程最为复杂、核心的过程，下面其中部分步骤：
  - 流程[2.7]：会调用到resolveActivity()，借助PackageManager来查询系统中所有符合要求的Activity，当存在多个满足条件的Activity则会弹框让用户来选择;
  - 流程[2.8]：创建ActivityRecord对象，并检查是否运行App切换，然后再处理mPendingActivityLaunches中的activity;
  - 流程[2.9]：为Activity找到或创建新的Task对象，设置flags信息；
  - 流程[2.13]：当没有处于非finishing状态的Activity，则直接回到桌面； 否则，当mResumedActivity不为空则执行`startPausingLocked`()暂停该activity;然后再进入`startSpecificActivityLocked`()环节;
  - 流程[2.14]：当目标进程已存在则直接进入流程[2.17]，当进程不存在则创建进程，经过层层调用还是会进入流程[2.17];
  - 流程[2.17]：system_server进程利用的ATP(Binder Client)，经过Binder，程序接下来进入目标进程。
- 流程[**2.19 ~2.18**]:运行在目标进程，通过Handler消息机制，该进程中的Binder线程向主线程发送`H.LAUNCH_ACTIVITY`，最终会通过反射创建目标Activity，然后进入onCreate()生命周期。

从另一个角度下图来概括：

![start_activity_process](assets/start_activity_process.jpg)

启动流程：

1. 点击桌面App图标，Launcher进程采用Binder IPC向system_server进程发起startActivity请求；
2. system_server进程接收到请求后，向zygote进程发送创建进程的请求；
3. Zygote进程fork出新的子进程，即App进程；
4. App进程，通过Binder IPC向sytem_server进程发起attachApplication请求；
5. system_server进程在收到请求后，进行一系列准备工作后，再通过binder IPC向App进程发送scheduleLaunchActivity请求；
6. App进程的binder线程（ApplicationThread）在收到请求后，通过handler向主线程发送LAUNCH_ACTIVITY消息；
7. 主线程在收到Message后，通过发射机制创建目标Activity，并回调Activity.onCreate()等方法。

到此，App便正式启动，开始进入Activity生命周期，执行完onCreate/onStart/onResume方法，UI渲染结束后便可以看到App的主界面。 启动Activity较为复杂，后续计划再进一步讲解生命周期过程与系统是如何交互，以及UI渲染过程，敬请期待。

# 参考

《Android 开发艺术探索》9.2

[startActivity启动过程分析](http://gityuan.com/2016/03/12/start-activity/)