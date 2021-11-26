> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 [segmentfault.com](https://segmentfault.com/a/1190000019373275)

> 前言：随着安卓 APP 规模越来越大，代码越来越多，各种疑难杂症问题也随之出现。

**出品 | 滴滴技术**  
**作者 | 江义旺**

前言：随着安卓 APP 规模越来越大，代码越来越多，各种疑难杂症问题也随之出现。比较常见的一个问题就是 GC finalize() 方法出现 java.util.concurrent.TimeoutException，这类问题难查难解，困扰了很多开发者。那么这类问题是怎么出现的呢？有什么解决办法呢？这篇文章为将探索 finalize() timeout 的原因和解决方案，分享我们的踩坑经验，希望对遇到此类问题的开发者有所帮助。

在一些大型安卓 APP 中，经常会遇到一个奇怪的 BUG：ava.util.concurrent.TimeoutException

其表现为对象的 finalize() 方法超时，如 android.content.res.AssetManager.finalize() timed out after 10 seconds 。

此前滴滴出行安卓端曾长期受此 BUG 的影响，每天有一些用户会因此遇到 Crash，经过深度分析，最终找到有效解决方案。这篇文章将对这个 BUG 的来龙去脉以及我们的解决方案进行分析。

▍问题详情

finalize() TimeoutException 发生在很多类中，典型的 Crash 堆栈如：

```shell
1 java.util.concurrent.TimeoutException: android.content.res.AssetManager$AssetInputStream.finalize() timed out after 15 seconds
2 at android.content.res.AssetManager$AssetInputStream.close(AssetManager.java:559)
3 at android.content.res.AssetManager$AssetInputStream.finalize(AssetManager.java:592)
4 at java.lang.Daemons$FinalizerDaemon.doFinalize(Daemons.java:187)
5 at java.lang.Daemons$FinalizerDaemon.run(Daemons.java:170)
6 at java.lang.Thread.run(Thread.java:841)
```

△ 左滑浏览全貌

这类 Crash 都是发生在 java.lang.Daemons$FinalizerDaemon.doFinalize 方法中，直接原因是对象的 finalize() 方法执行超时。系统版本从 Android 4.x 版本到 8.1 版本都有分布，低版本分布较多，出错的类有系统的类，也有我们自己的类。由于该问题在 4.x 版本中最具有代表性，下面我们将基于 AOSP 4.4 源码进行分析：

▍源码分析

首先从 Daemons 和 FinalizerDaemon 的由来开始分析，Daemons 开始于 Zygote 进程：Zygote 创建新进程后，通过 ZygoteHooks 类调用了 Daemons 类的 start() 方法，在 start() 方法中启动了 FinalizerDaemon，FinalizerWatchdogDaemon 等关联的守护线程。

```java
 public final class Daemons {
    ...
    private static final long MAX_FINALIZE_NANOS = 10L * NANOS_PER_SECOND;
    public static void start() {
        FinalizerDaemon.INSTANCE.start();
        FinalizerWatchdogDaemon.INSTANCE.start();
        ...
    }
    public static void stop() {
        FinalizerDaemon.INSTANCE.stop();
        FinalizerWatchdogDaemon.INSTANCE.stop();
        ...
    }
}
```

△ 左滑浏览全貌

Daemons 类主要处理 GC 相关操作，start() 方法调用时启动了 5 个守护线程，其中有 2 个守护线程和这个 BUG 具有直接的关系。

▍FinalizerDaemon 析构守护线程

对于重写了成员函数 finalize() 的类，在对象创建时会新建一个 FinalizerReference 对象，这个对象封装了原对象。当原对象没有被其他对象引用时，这个对象不会被 GC 马上清除掉，而是被放入 FinalizerReference 的链表中。FinalizerDaemon 线程循环取出链表里面的对象，执行它们的 finalize() 方法，并且清除和对应 FinalizerReference 对象引用关系，对应的 FinalizerReference 对象在下次执行 GC 时就会被清理掉。

```java
private static class FinalizerDaemon extends Daemon {
    ...
    @Override public void run() {
        while (isRunning()) {
            // Take a reference, blocking until one is ready or the thread should stop
            try {
                doFinalize((FinalizerReference<?>) queue.remove());
            } catch (InterruptedException ignored) {
            }
        }
    }
    @FindBugsSuppressWarnings("FI_EXPLICIT_INVOCATION")
    private void doFinalize(FinalizerReference<?> reference) {
        ...
        try {
            finalizingStartedNanos = System.nanoTime();
            finalizingObject = object;
            synchronized (FinalizerWatchdogDaemon.INSTANCE) {
                FinalizerWatchdogDaemon.INSTANCE.notify();
            }
            object.finalize();
        } catch (Throwable ex) {
            ...
        } finally {
            finalizingObject = null;
        }
    }
}
```

△ 左滑浏览全貌

▍FinalizerWatchdogDaemon 析构监护守护线程

析构监护守护线程用来监控 FinalizerDaemon 线程的执行，采用 Watchdog 计时器机制。当 FinalizerDaemon 线程开始执行对象的 finalize() 方法时，FinalizerWatchdogDaemon 线程会启动一个计时器，当计时器时间到了之后，检测 FinalizerDaemon 中是否还有正在执行 finalize() 的对象。检测到有对象存在后就视为 finalize() 方法执行超时，就会产生 TimeoutException 异常。

```java
private static class FinalizerWatchdogDaemon extends Daemon {
    ...
    @Override public void run() {
        while (isRunning()) {
            ...
            boolean finalized = waitForFinalization(object);
            if (!finalized && !VMRuntime.getRuntime().isDebuggerActive()) {
                finalizerTimedOut(object);
                break;
            }
        }
    }
    ...
    private boolean waitForFinalization(Object object) {
        sleepFor(FinalizerDaemon.INSTANCE.finalizingStartedNanos, MAX_FINALIZE_NANOS);
        return object != FinalizerDaemon.INSTANCE.finalizingObject;//当sleep时间到之后，检测 FinalizerDaemon 线程中当前正在执行 finalize 的对象是否存在，如果存在说明 finalize() 方法超时
    }
    private static void finalizerTimedOut(Object object) {
        String message = object.getClass().getName() + ".finalize() timed out after "
                + (MAX_FINALIZE_NANOS / NANOS_PER_SECOND) + " seconds";
        Exception syntheticException = new TimeoutException(message);
        syntheticException.setStackTrace(FinalizerDaemon.INSTANCE.getStackTrace());
        Thread.UncaughtExceptionHandler h = Thread.getDefaultUncaughtExceptionHandler();
        ...
        h.uncaughtException(Thread.currentThread(), syntheticException);
    }
}
```

△ 左滑浏览全貌

由源码可以看出，该 Crash 是在 FinalizerWatchdogDaemon 的线程中创建了一个 TimeoutException 传给 Thread 类的 defaultUncaughtExceptionHandler 处理造成的。由于异常中填充了 FinalizerDaemon 的堆栈，之所以堆栈中没有出现和 FinalizerWatchdogDaemon 相关的类。

▍原因分析

finalize() 导致的 TimeoutException Crash 非常普遍，很多 APP 都面临着这个问题。使用 finalize() TimeoutException 为关键词在搜索引擎或者 Stack Overflow 上能搜到非常多的反馈和提问，技术网站上对于这个问题的原因分析大概有两种：

▍对象 finalize() 方法耗时较长

当 finalize() 方法中有耗时操作时，可能会出现方法执行超时。耗时操作一般有两种情况，一是方法内部确实有比较耗时的操作，比如 IO 操作，线程休眠等。另外有种线程同步耗时的情况也需要注意：有的对象在执行 finalize() 方法时需要线程同步操作，如果长时间拿不到锁，可能会导致超时，如 android.content.res.AssetManager$AssetInputStream 类：

```java
public final class AssetInputStream extends InputStream {
    ...
    public final void close() throws IOException {
        synchronized (AssetManager.this) {
            ...
        }
    }
    ...
    protected void finalize() throws Throwable {
        close();
    }
    ...
 }
```

△ 左滑浏览全貌

AssetManager 的内部类 AssetInputStream 在执行 finalize() 方法时调用 close() 方法时需要拿到外部类 AssetManager 对象锁， 而在 AssetManager 类中几乎所有的方法运行时都需要拿到同样的锁，如果 AssetManager 连续加载了大量资源或者加载资源是耗时较长，就有可能导致内部类对象 AssetInputStream 在执行 finalize() 时长时间拿不到锁而导致方法执行超时。

```java
public final class AssetManager implements AutoCloseable {
    ...
    /*package*/ final CharSequence getResourceText(int ident) {
        synchronized (this) {
            ...
        }
        return null;
    }
    ...
    public final InputStream open(String fileName, int accessMode) throws IOException {
        synchronized (this) {
            ...
        }
        throw new FileNotFoundException("Asset file: " + fileName);
    }
    ...
 }
```

△ 左滑浏览全貌

▍5.0 版本以下机型 GC 过程中 CPU 休眠导致

有种观点认为系统可能会在执行 finalize() 方法时进入休眠， 然后被唤醒恢复运行后，会使用现在的时间戳和执行 finalize() 之前的时间戳计算耗时，如果休眠时间比较长，就会出现 TimeoutException。

详情请见∞

确实这两个原因能够导致 finalize() 方法超时，但是从 Crash 的机型分布上看大部分是发生在系统类，另外在 5.0 以上版本也有大量出现，因此我们认为可能也有其他原因导致此类问题：

▍IO 负载过高

许多类的 finalize() 都需要释放 IO 资源，当 APP 打开的文件数目过多，或者在多进程或多线程并发读取磁盘的情况下，随着并发数的增加，磁盘 IO 效率将大大下降，导致 finalize() 方法中的 IO 操作运行缓慢导致超时。

▍FinalizerDaemon 中线程优先级过低

FinalizerDaemon 中运行的线程是一个守护线程，该线程优先级一般为默认级别 (nice=0)，其他高优先级线程获得了更多的 CPU 时间，在一些极端情况下高优先级线程抢占了大部分 CPU 时间，FinalizerDaemon 线程只能在 CPU 空闲时运行，这种情况也可能会导致超时情况的发生，(从 Android 8.0 版本开始，FinalizerDaemon 中守护线程优先级已经被提高，此类问题已经大幅减少)

▍解决方案

当问题出现后，我们应该找到问题的根本原因，从根源上去解决。然而对于这个问题来说却不太容易实现，和其他问题不同，这类问题原因比较复杂，有系统原因，也有 APP 自身的原因，比较难以定位，也难以系统性解决。

▍理想措施

理论上我们可以做的措施有：

1.  减少对 finalize() 方法的依赖，尽量不依靠 finalize() 方法释放资源，手动处理资源释放逻辑。
2.  减少 finalizable 对象个数，即减少有 finalize() 方法的对象创建，降低 finalizable 对象 GC 次数。

3.finalize() 方法内尽量减少耗时以及线程同步时间。

1.  减少高优先级线程的创建和使用，降低高优先级线程的 CPU 使用率。

▍止损措施

理想情况下的措施，可以从根本上解决此类问题，但现实情况下却不太容易完全做到，对一些大型 APP 来说更难以彻底解决。那么在解决问题的过程中，有没有别的办法能够缓解或止损呢？总结了技术网站上现有的方案后，可以总结为以下几种：

*   手动修改 finalize() 方法超时时间

```java
  try {
    Class<?> c = Class.forName(“java.lang.Daemons”);
    Field maxField = c.getDeclaredField(“MAX_FINALIZE_NANOS”);
    maxField.setAccessible(true);
    maxField.set(null, Long.MAX_VALUE);
 } catch (Exception e) {
    ...
 }
```

△ 左滑浏览全貌

[详情请见∞](https://link.segmentfault.com/?enc=iRJ0QELMXoHVP%2FDt0wMT5Q%3D%3D.6MptDgd8RrzlaazEmDY62WYH7Re6C66LHmCcAYjy0RMnlKIisxrHxLRCsJuBUU1RKJD0VRAfThWZqUjfkyVGbfSxYEw3utoJRqSaFYIk1Oq4C6tM0Hl%2BA4d9fcOyd4jlxgoWXv6ZG2SVLKJwR2lrE%2Bf2zhzoNcoJu8sPcYkazyg%3D)

这种方案思路是有效的，但是这种方法却是无效的。Daemons 类中 的 MAX_FINALIZE_NANOS 是个 long 型的静态常量，代码中出现的 MAX_FINALIZE_NANOS 字段在编译期就会被编译器替换成常量，因此运行期修改是不起作用的。MAX_FINALIZE_NANOS 默认值是 10s，国内厂商常常会修改这个值，一般有 15s，30s，60s，120s，我们可以推测厂商修改这个值也是为了加大超时的阙值，从而缓解此类 Crash。

*   手动停掉 FinalizerWatchdogDaemon 线程

```java
   try {
        Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
        Method method = clazz.getSuperclass().getDeclaredMethod("stop");
        method.setAccessible(true);
        Field field = clazz.getDeclaredField("INSTANCE");
        field.setAccessible(true);
        method.invoke(field.get(null));
    } catch (Throwable e) {
        e.printStackTrace();
    }
```

△ 左滑浏览全貌

[详情请见∞](https://link.segmentfault.com/?enc=zsw0SFVEROQ4bDcSLWjgfw%3D%3D.cfWHM%2B6NFOZmng03HoTGQfP4Cng%2Fsj3jjfecftaHOmg%2Fl6VAcmXMZqLEg8HEfz9FPfu6tmWtBzMdj9kAGcVXg%2Bai%2B9fu5UFUYZERecLUayCoJBAYqlgscmECHU798W16zTmNlna5oi3wm%2BK9VxxUEL1XUhieQdRyba52gh%2BP%2BcA%3D)

这种方案利用反射 FinalizerWatchdogDaemon 的 stop() 方法，以使 FinalizerWatchdogDaemon 计时器功能永远停止。当 finalize() 方法出现超时， FinalizerWatchdogDaemon 因为已经停止而不会抛出异常。这种方案也存在明显的缺点：

1.  在 Android 5.1 版本以下系统中，当 FinalizerDaemon 正在执行对象的 finalize() 方法时，调用 FinalizerWatchdogDaemon 的 stop() 方法，将导致 run() 方法正常逻辑被打断，错误判断为 finalize() 超时，直接抛出 TimeoutException。
2.  Android 9.0 版本开始限制 Private API 调用，不能再使用反射调用 Daemons 以及 FinalizerWatchdogDaemon 类方法。

▍终极方案

这些方案都是阻止 FinalizerWatchdogDaemon 的正常运行，避免出现 Crash，从原理上还是具有可行性的：finalize() 方法虽然超时，但是当 CPU 资源充裕时，FinalizerDaemon 线程还是可以获得充足的 CPU 时间，从而获得了继续运行的机会，最大可能的延长了 APP 的存活时间。但是这些方案或多或少都是有缺陷的，那么有其他更好的办法吗？

What should we do? We just ignore it.

我们的方案就是忽略这个 Crash，那么怎么能够忽略这个 Crash 呢？首先我们梳理一下这个 Crash 的出现过程：

1.  FinalizerDaemon 执行对象 finalize() 超时。
2.  FinalizerWatchdogDaemon 检测到超时后，构造异常交给 Thread 的 defaultUncaughtExceptionHandler 调用 uncaughtException() 方法处理。
3.  APP 停止运行。

Thread 类的 defaultUncaughtExceptionHandler 我们很熟悉了，Java Crash 捕获一般都是通过设置 Thread.setDefaultUncaughtExceptionHandler() 方法设置一个自定义的 UncaughtExceptionHandler ，处理异常后通过链式调用，最后交给系统默认的 UncaughtExceptionHandler 去处理，在 Android 中默认的 UncaughtExceptionHandler 逻辑如下：

```java
public class RuntimeInit {
    ...
   private static class UncaughtHandler implements Thread.UncaughtExceptionHandler {
       public void uncaughtException(Thread t, Throwable e) {
           try {
                ...
               // Bring up crash dialog, wait for it to be dismissed 展示APP停止运行对话框
               ActivityManagerNative.getDefault().handleApplicationCrash(
                       mApplicationObject, new ApplicationErrorReport.CrashInfo(e));
           } catch (Throwable t2) {
                ...
           } finally {
               // Try everything to make sure this process goes away.
               Process.killProcess(Process.myPid()); //退出进程
               System.exit(10);
           }
       }
   }
    private static final void commonInit() {
        ...
        /* set default handler; this applies to all threads in the VM */
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());
        ...
    }
 }
```

△ 左滑浏览全貌

从系统默认的 UncaughtExceptionHandler 中可以看出，APP Crash 时弹出的停止运行对话框以及退出进程操作都是在这里处理中处理的，那么只要不让这个代码继续执行就可以阻止 APP 停止运行了。基于这个思路可以将这个方案表示为如下的代码：

```java
 final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
 Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (t.getName().equals("FinalizerWatchdogDaemon") && e instanceof TimeoutException) {
             //ignore it
        } else {
            defaultUncaughtExceptionHandler.uncaughtException(t, e);
        }
    }
 });
```

△ 左滑浏览全貌

*   可行性

这种方案在 FinalizerWatchdogDaemon 出现 TimeoutException 时主动忽略这个异常，阻断 UncaughtExceptionHandler 链式调用，使系统默认的 UncaughtExceptionHandler 不会被调用，APP 就不会停止运行而继续存活下去。由于这个过程用户无感知，对用户无明显影响，可以最大限度的减少对用户的影响。

*   优点

1. 对系统侵入性小，不中断 FinalizerWatchdogDaemon 的运行。

2.Thread.setDefaultUncaughtExceptionHandler() 方法是公开方法，兼容性比较好，可以适配目前所有 Android 版本。

▍总结

不管什么样的缓解措施，都是治标不治本，没有从根源上解决。对于这类问题来说，虽然人为阻止了 Crash，避免了 APP 停止，APP 能够继续运行，但是 finalize() 超时还是客观存在的，如果 finalize() 一直超时的状况得不到缓解，将会导致 FinalizerDaemon 中 FinalizerReference 队列不断增长，最终出现 OOM 。因此还需要从一点一滴做起，优化代码结构，培养良好的代码习惯，从而彻底解决这个问题。当然 BUG 不断，优化不止，在解决问题的路上，缓解止损措施也是非常重要的手段。谁能说能抓老鼠的白猫不是好猫呢？

▍END  
