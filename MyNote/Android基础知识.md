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
# Activity（19.8.5）

## 监听所有Actvity的生命周期回调

```java
Applicaiton —> registerActivityLifecycleCallbacks();
```

# Fragment（19.7.5）

## Fragment与Activity生命周期对照图
[原图地址](https://github.com/xxv/android-lifecycle)
![](https://github.com/xxv/android-lifecycle/raw/master/complete_android_fragment_lifecycle.png)

## Fragment常用方法

1. `isResume`

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



# Window

19年7月16日

如何给



# RecyclerView

## 基本概念

参考视频：

## DiffUtil

[参考](https://blog.csdn.net/zxt0601/article/details/52562770)

## 优化




# 工作方式篇
## 提交代码
1. **git d review code**：在提交代码的时候一定要review code 看自己修改了那些文件，有没有被自己没有修改但是格式变化的文件
2. **及时拉去最新代码**：每天到了公司之后，第一件事就是拉去代码。在提交代码之后也要拉代码，保证手里的代码是最新的。避免提交之后有冲突
## 写代码
1. **修改代码**：在没有读懂原有代码逻辑的时候不要轻易修改，没看懂不要写！！！！！
2. **避免波及**：在修改资源文件的时候一定要看有没有其他地方使用！！！
3. **注意细节**：在修改文案的时候一定要注意有没有文案被弄错了。
4. **写代码**： 写代码的时候首先要想好逻辑，再动手写。

## 工作顺序
1. 第一步就是分析需求设计代码
2. 写大概的UI
3. 主要是逻辑的编辑，UI可以后面细调。

## ViewHolder
在ViweHolder中禁止注册EventBuss，因为无法得知VH什么时候会被销毁，

#代码规范
1. 慎重public，多使用public
2. 换行：a. 方法之间不能不换号，但是换行不能超过三行。
      ​     b. 代码块之间不要随意换行，上下文没有直接联系的时候才能换行
3. 如果在一个类中一个字段为private不要着急改成public，而是要看看有没有对应的getXX().
4. 不要轻易使用public关键字！！！一般直接写private，在需要将字段暴露的时候先检查时候已经有方法实现了
