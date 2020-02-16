[TOC]

# 监听所有Actvity的生命周期回调

```java
Applicaiton —> registerActivityLifecycleCallbacks();
```



# onWindowFocusChanged





# onSaveInstanceState(Bundle)

```java
protected void onSaveInstanceState (Bundle outState)
```



> onSaveInstanceSate()是防止Activity在内存不足等情况下被系统杀死而导致数据丢失。

## 调用时机

一直以来对于 onSaveInstanceState(Bundle) 调用时机的理解存在误区！

并不是说系统在异常杀死Activity前会调用 onSaveInstanceSate(Bundle)，而是在某些系统认为有可能会导致Activity被回收的情况下，预先调用了 onSaveInstanceSate(Bundle) 方法。

例如：

1. 点击Home键回到桌面。
2. 启动另外一个Activity
3. 从最近应用中启动其他App
4. 锁屏的时候
5. 横竖屏切换的时候

这些情况下，onSaveInstanceSate(Bundle) 都会被调用



## 保存方式

Activity作为根部，根据视图树一层一层遍历 Child 的 onSavenInstanceSate(Bundle)方法，恢复的时候一样一层层的调用 onRestorInstanceSate(Bundle)



## 恢复

有两个方法可供恢复 onSaveInstanceState(Bundle) 中存储的数据

1. onCreate(Bundle)
2. onResortInstanceSate(Bundle)

在 onCreate 方法中可以通过判定 Bundle是否为 null 的方式来判定此Activity是否是恢复创建的，如果` Bunlde ！= null` 则表示是恢复的。

如果是恢复启动， onRestorInstaceState(Bundle) 方法的调用是必然的，我们可以

## 生命周期

如上所述 onSaveInstanceState 并不保证每次都会调用，而且他的生命周期反应也不固定，但可以确认的是 onSaveInstanceSate 与 onStop 之间的关系

1. 在Android P（api 28）以后 onSaveInstanceState 将会在 onStop 之后调用
2. 在 api 28 之前会在 onStop之前调用，但是和 onPuase 之前的关系不确定，可能在 onPause 之前，也可能只之后。



用于恢复的方法：onResotrInstanceSate(Bundle)  会在 onStart 与 onPostCreate(Bundle）之间调用

## 参考

[Android 文档 —— 保存界面状态](<https://developer.android.com/topic/libraries/architecture/saving-states.html?hl=zh_cn>)

