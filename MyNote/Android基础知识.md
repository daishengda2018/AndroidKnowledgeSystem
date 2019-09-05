[TOC]


# 内存泄漏

## Handler 造成的内存泄漏 (19.6.25日被骂，超级尴尬)

**问题描述**
Handler中的Message对象会持有Handler的饮用，而Handler如果是一个匿名内部类的方式使用：

```java
mHandler = new Handler(Looper.getMiainLooper()) {
    @Override
    public void handleMessage(Message msg) {
    }
}
```
Handler则会持有Activity的强引用，在Activity需要销毁的时候，Looper还在运行中，则内存泄漏。

**处理方案**

1. 在Activity `onDestory`、View `onDetachedFromWindos`中调用 `removeCallAndMessage(null);`,但是Activity`onDestory`在异常情况下不会被调用，所以需要加上第二种方式。
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
结局
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




# Bitmap的处理
## 内存泄漏
## inSampleSize

为了防止内存泄漏，可以通过指定 `inSampleSize` 的值缩小图片的大小。主要的步骤就是通过设置 `options.inJustDecodeBound = true`的方式只将图片加载到内存中，而不输出（也就是说：此时`BitmapFactory.decodeResource(getResources(), resId, options);` 返回的结果为null！！！！） 

在获取 `options.outWidth & options.outHeight` 之后需要将 `inJustDecodeBound ` 设置为 false 



inSampleSize的含义是将原来图片缩小为原来的：1/inSampleSize。Api上写明**inSampleSize 必须是2的整数倍，如果指定的大小不是2的整数倍，则在运行时对指定值处理：取最接近2整数倍的值**

```java
   /**
     * 从资源文件中获取Bitmap
     *
     * @param resId 资源ID
     * @return Bitmap
     */
    private Bitmap getBitmap(@DrawableRes int resId) {
        if (resId == 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = BitmapUtils.calculateInSampleSize(options, getWidth(), getHeight());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        Matrix matrix = getMatrix(bitmap);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
```



```java
   /**
   	 * 计算采样率代码
     * @param options
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @return
     */
    public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
```

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





# Android 消息机制 —— Handler

Android 的消息机制主要指的是 Handler 的运行机制，从开发者的角度来说 Handler 是 Android 消息机制的上层接口，而底层的逻辑则是由 MessageQueue、 Looper来完成的。

Handler 的设计目的是为了解决 Android 主线程中不能做耗时操作而又只有主线程才能访问 UI 的矛盾。通过 Handler 消息机制可以让开发者在子线程中完成耗时操作后在主线程中更新UI。



**这里要思考一个问题：为什么 Android 非要规定只有主线程才能更新 UI 呢？**

因为 Android 的所有 View 控件都不是线程安全的，如果在多线程中并发访问很可能造成意想不到的结果。对于加锁这种方案也不可取，首先加锁之后会让 UI 访问逻辑变的很复杂，开发者需要时刻考虑多线程并发将会带来的问题，其次锁机制太重了它会严重影响 UI 访问效率。介于这两个缺点，最简单且高效的方法就是采用单线程的方式访问 UI。Handler 机制应运而生。



## ThreadLocal

ThreadLocal 并不是 Thread ，他的特点很有意思: 每一个线程存储的值相互隔离的：

```java
public class TreadLocalDemo {
    // 就算设置为 static 结果也是一样的
    ThreadLocal<Boolean> mThreadLocal = new ThreadLocal<Boolean>();

    public void runDemo() {
        mThreadLocal.set(true);
        System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
        new Thread("Thread#1") {
            @Override
            public void run() {
                super.run();
                mThreadLocal.set(false);
                System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
            }
        }.start();

        new Thread("Thread#2") {
            @Override
            public void run() {
                super.run();
                System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
            }
        }.start();
        System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
    }
}
```

运行后的结果：

![image-20190905231656636](assets/image-20190905231656636.png)

线程之间的存储的值相互独立的彼此不受影响！



- MessageQueue
- Looper
- 

默认情况下，在什么线程创建Handler， post 里面执行的内容就会执行在那个线程里。

可以手动指定线程 

```java
Handler mHandler = new Handler(Looper.getMainLooper());
```

* Handler 是如何将详细 post 到启动线程中的呢？
* 阅读 Handler Looper HandlerThread 源码。



## ThreadLocal

并不是一个线程，而是一个线程间不贡献的内存空间。

Looper.MyLooper 有使用到





