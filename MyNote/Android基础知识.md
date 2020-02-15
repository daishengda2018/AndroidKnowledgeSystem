[TOC]


# 内存泄漏

## Handler 造成的内存泄漏 (19.6.25日被骂，超级尴尬)

**问题描述**
Handler 中的 Message 对象会持有 Handler 的引用，而 Handler 如果是一个匿名内部类的方式使用：

```java
mHandler = new Handler(Looper.getMiainLooper()) {
    @Override
    public void handleMessage(Message msg) {
    }
}
```
Handler则会持有Activity的强引用，在Activity需要销毁的时候，Looper还在运行中，则内存泄漏。

**处理方案**

1. 在Activity `onDestory`、View `onDetachedFromWindos`中调用 `removeCallAndMessage(null);`,**但是Activity`onDestory`在异常情况下不会被调用**，所以需要加上第二种方式。
2. 创建Handler匿名内部类、如果需要依赖外部类，可以传递一个弱引用进去
```java
private static class MyHandler extend Handler {
    private WeakReference mReference;
    MyHandler(Actvity activity) {
        new WeakReference(actvity);
    }
    @Override
    public void handleMessage(Message msg) {
        if (mReference.get() == null ){
            removeCallAndMessage(null);
            return;
        }
        // to do sth.
        mReference.get().todo();
    }
}
```
## 线程造成的内存泄漏

**JVM 不会回收正在运行中的线程**，所有如果 Thread 中运行着一个长任务并且引用了 Activity，那么就会造成内存泄漏，例如 AsyncTask 造成内存泄漏的本质其实也是此原因，所以 AsyncTask 的内存泄漏并不是他自身的特点，而是所有线程的都会造成内存泄漏的风险。

但是，需要运行的任务时间是短暂的例如开发者设定了一个短暂的运行时间，那么其实可以忽略这种，当然可以使用 Activity 弱引用的方式使用线程。



# Activity（19.8.5）

## 监听所有Actvity的生命周期回调

```java
Applicaiton —> registerActivityLifecycleCallbacks();
```

## 设置进入和退出动画

* 【anim】in_from_up.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:duration="300"
        android:fromYDelta="100%p"
        android:toYDelta="0%p" />

</set>
```

* 【anim】out_to_down.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:duration="300"
        android:fromYDelta="0%p"
        android:toYDelta="100%p" />

</set>
```

* 【style.xml】

```java
 <!--自下而上进入 自上而下退出 -->
    <style name="AppAnimationTheme" parent="继承默认app主题即可">
        <!-- 将Activity的Theme设置成透明 -->
        <item name="android:windowBackground">@null</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:activityOpenEnterAnimation">@anim/in_from_up</item>
        <item name="android:activityOpenExitAnimation">@anim/in_from_up</item>
        <item name="android:activityCloseEnterAnimation">@anim/out_to_down</item>
        <item name="android:activityCloseExitAnimation">@anim/out_to_down</item>
    </style>

```

* 【AndroidManifest.xml】设置主题

```xml
<activity android:theme="@style/AppAnimationTheme" />
```

* 【Activity跳转页面】

```java
Intent intent = new Intent();
startActivity(intent);
// overridePendingTransition 是 Activity 的方法
overridePendingTransition(R.anim.in_from_up, android.R.anim.fade_out);
```
* 【Activity目标页面】重写finish

```java
@Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.out_to_down, R.anim.out_to_down);
    }
```

## 设置透明背景

* 【style.xml】

```xml
  <style name="Transparent" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowIsTranslucent">true</item>
    </style>
```

*  【AndroidManifest.xml】设置主题

```xml
<activit
            android:name=".join.RequestJoinListActivity"
            android:screenOrientation="portrait"
            android:theme="@style/Transparent" />
```

## 设置状态栏颜色

```java
StatusBarUtil.setColor(this, Color.TRANSPARENT);
```



# Fragment（19.7.5）

## Fragment与Activity生命周期对照图
[原图地址](https://github.com/xxv/android-lifecycle)
![](https://github.com/xxv/android-lifecycle/raw/master/complete_android_fragment_lifecycle.png)

## Fragment常用方法

1. `isResumed`

##  选择正确的 Fragment#commitXXX() 函数
**异常：Can not perform this action after onSaveInstaceState()**
当Actvity在后台被回收的时候会调用`onSaveInstanceState()`方法保存状态和数据，**直到再回到Avtivity之前（`onResume()`）提交FragmentTransaction就会报错**（这种情况一般出现在其他Activity回调让当前页面执行FragmentTransaction的时候）

如何解决

1. `commit()`
    提交不会立即执行，而是排到了主线程等待准备好的时候执行

2. `commitAllowingStateLoss()`
    **允许在Actvity保存状态之后提交，即允许状态丢失。**
    与commit一样但是允许Activity的状态保存之后提交。他的实现是和`commit`几乎一样的，唯一的区别在于`commit`执行之后FragmentManager都会检测是否已经保存了状态（`onSaveInstaceState()`），如果已经保存状态再次使用`commit`会抛出异常：`java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState`
    如果在`onSaveInstanceState()`之后调用`commitAllowingStateLoss()` FragmentManager保存的所有状态可能被丢失，而且重新启动之后在 onSaveInstaceStae() 之后添加和删除的 Fragment 也会丢失。
    **如果说保存状态对于业务需求不重要，则使用`onSaveInstanceState()`**
    使用
3. `commitNow()`
    立即将事务提交

4. `commitNowAllowingStateLoss()`
    立即提交而且允许在保存状态之后提交。

## Fragment的重叠问题
**原因** : 这种情况出现在使用`add\show\hind`时，在异常情况下 Activity会被系统回收，此时Fragmen中的`onSaveInstaceState()`也会被Avtivity的`onSaveInstaceState()`递归调用，在恢复的时候Fragmeng会重新创建。
**解决方案** ：
1. 直接注释掉Activity或者Fragment中的`onSaveInstaceState()` 然后使用`commitAllowingLossSate()`提交事务
2. 在创建的时候的时候添加Tag并存储起来
```java
Fragment fragment = getSupportFragmentManager().findFragmentByTag("FragmentA");
if (fragment == null) {
    mFragmentA = new FragmentA();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.container, mFragmentA, "FragmentA").commit();
} else {
    mFragmentA = (FragmentA) fragment;
}
```
3. 
    通过savedInstanceState是否null，判断是否已经创建过
```java
if (savedInstanceState == null) {
    mFragmentA = new FragmentA();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.container, mFragmentA, "FragmentA").commit();
} else {
    mFragmentA = (FragmentA) getSupportFragmentManager().findFragmentByTag("FragmentA");
}
```
## Fragment转场动画
想要给Fragmengt设置进栈和出栈动画，需要使用四个参数的方法`setCustomAnimations(enter exit, popEnter, popExit)` -> `setCustomAnimations(进 exit, popEnter, 出)`，两个参数的方法`setCustomAnimations(enter, exit)`只能设置进场动画.
```java
final FragmentManager fm = getSupportFragmentManager();
final FragmentTransaction ft = fm.beginTransaction();
ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
  .add(R.id.fragment_container, new SomeFragment(), FRAGMENT_TAG)
  .addToBackStack(FRAGMENT_TAG)
  .commit();
```

## Fragment的坑
[Fragment全解析系列（一）：那些年踩过的坑](https://www.jianshu.com/p/d9143a92ad94)
[Fragment的那些坑](http://toughcoder.net/blog/2015/04/30/android-fragment-the-bad-parts/)



