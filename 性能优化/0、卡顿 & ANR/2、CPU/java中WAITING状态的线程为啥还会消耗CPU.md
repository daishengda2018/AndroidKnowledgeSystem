> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 [juejin.cn](https://juejin.cn/post/6844904001067040781#heading-6)

> 刚刚过去的双十一, 公司订单量又翻了一倍. 就在老板坐在办公室里面偷偷笑的同时, 坐在工位上的我们却是一直瑟瑟发抖. 面对 zabbix 里面时不时蹦出来的一条条 CPU 告警, 默默地祈祷着不要出问题. 当然, ......

### 背景

刚刚过去的双十一, 公司订单量又翻了一倍. 就在老板坐在办公室里面偷偷笑的同时, 坐在工位上的我们却是一直瑟瑟发抖. 面对 zabbix 里面时不时蹦出来的一条条 CPU 告警, 默默地祈祷着不要出问题.

当然, 祈祷是解决不了问题的, 即使是开过光的服务器也不行. CPU 告警了, 还得老老实实地去看为啥 CPU 飚起来了.

接下来就是 CPU 排查三部曲

```
1. top -Hp $pid 找到最耗CPU的线程. 
2. 将最耗CPU的线程ID转成16进制
3. 打印jstack, 到jstack里面查这个线程在干嘛

```

当然 如果你线上环境有装 arthas 等工具的话, 直接 thread -n 就可以打印出最耗 cpu 的 n 个线程的堆栈, 三个步骤一起帮你做了.

最后找到最耗 cpu 的线程堆栈如下:

```
"operate-api-1-thread-6" #1522 prio=5 os_prio=0 tid=0x00007f4b7006f800 nid=0x1b67c waiting on condition [0x00007f4ac8c4a000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000006c10828c8> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.parkAndCheckInterrupt(AbstractQueuedSynchronizer.java:836)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued(AbstractQueuedSynchronizer.java:870)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:1199)
	at java.util.concurrent.locks.ReentrantLock$NonfairSync.lock(ReentrantLock.java:209)
	at java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:285)
	at ch.qos.logback.core.OutputStreamAppender.subAppend(OutputStreamAppender.java:210)
	at ch.qos.logback.core.rolling.RollingFileAppender.subAppend(RollingFileAppender.java:235)
	at ch.qos.logback.core.OutputStreamAppender.append(OutputStreamAppender.java:100)
	at ch.qos.logback.core.UnsynchronizedAppenderBase.doAppend(UnsynchronizedAppenderBase.java:84)
	at ch.qos.logback.core.spi.AppenderAttachableImpl.appendLoopOnAppenders(AppenderAttachableImpl.java:51)
	at ch.qos.logback.classic.Logger.appendLoopOnAppenders(Logger.java:270)
	at ch.qos.logback.classic.Logger.callAppenders(Logger.java:257)
	at ch.qos.logback.classic.Logger.buildLoggingEventAndAppend(Logger.java:421)
	at ch.qos.logback.classic.Logger.filterAndLog_0_Or3Plus(Logger.java:383)
	at ch.qos.logback.classic.Logger.info(Logger.java:579)
	...

复制代码

```

值得一提的是, 类似的线程还有 800 多个... 只是部分没有消耗 CPU 而已

### 问题

很明显, 这是因为 logback 打印日志太多了造成的 (此时应有一个尴尬而不失礼貌的假笑).

当大家都纷纷转向讨论接下来如何优化 logback 和打日志的时候. 我却眉头一皱, 觉得事情并没有那么简单:

这个线程不是被 LockSupport.park 挂起了, 处于 WAITING 状态吗? 被挂起即代表放弃占用 CPU 了, 那为啥还会消耗 CPU 呢?

来看一下 LockSupport.park 的注释, 明确提到 park 的线程不会再被 CPU 调度了的:

```
   /**
     * Disables the current thread for thread scheduling purposes unless the
     * permit is available.
     *
     * <p>If the permit is available then it is consumed and the call
     * returns immediately; otherwise the current thread becomes disabled
     * for thread scheduling purposes and lies dormant until one of three
     * things happens:
     *
     */
    public static void park() {
        UNSAFE.park(false, 0L);
    }
复制代码

```

### 实验见真知

带着这个疑问, 我在 stackoverflow 搜索了一波, 发现还有不少人有这个疑问

1.  [stackoverflow.com/questions/1…](https://link.juejin.cn/?target=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F12972918%2Fwhy-does-park-unpark-have-60-cpu-usage "https://stackoverflow.com/questions/12972918/why-does-park-unpark-have-60-cpu-usage")
  
2.  [stackoverflow.com/questions/5…](https://link.juejin.cn/?target=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F52283308%2Fwhy-is-an-idle-java-thread-showing-high-cpu-usage "https://stackoverflow.com/questions/52283308/why-is-an-idle-java-thread-showing-high-cpu-usage")
  
3.  [stackoverflow.com/questions/1…](https://link.juejin.cn/?target=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F15990779%2Fhigh-cpu-within-object-wait "https://stackoverflow.com/questions/15990779/high-cpu-within-object-wait")
  
4.  [stackoverflow.com/questions/3…](https://link.juejin.cn/?target=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F38235700%2Fwaiting-threads-resource-consumption "https://stackoverflow.com/questions/38235700/waiting-threads-resource-consumption")
  
5.  [stackoverflow.com/questions/5…](https://link.juejin.cn/?target=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F5895895%2Fdoes-java-blocked-threads-take-up-more-cpu-resources "https://stackoverflow.com/questions/5895895/does-java-blocked-threads-take-up-more-cpu-resources")
  

上面好几个问题内容有点多, 我也懒得翻译了, 直接总结结论:

```
1. 处于waittig和blocked状态的线程都不会消耗CPU
2. 线程频繁地挂起和唤醒需要消耗CPU, 而且代价颇大
复制代码

```

但这是别人的结论, 到底是不是这样的呢. 下面我们结合 visualvm 来做一下实验.

#### 有问题的代码

首先来看一段肯定会消耗 100%CPU 的代码:

```
package com.test;

public class TestCpu {
    public static void main(String[] args) {
         while(true){

         }
    }
}
复制代码

```

visualvm 显示 CPU 确实消耗了 1 个核, main 线程也是占用了 100% 的 CPU:

![](images/16e8701019439433~tplv-t2oaga2asx-watermark.awebp) ![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2019/11/20/16e8701028f470a2~tplv-t2oaga2asx-watermark.awebp)

#### 被 park 的线程

然后来看一下 park 的线程是否会消耗 cpu

代码:

```
import java.util.concurrent.locks.LockSupport;

public class TestCpu {
    public static void main(String[] args) {
        while(true){
            LockSupport.park();
        }
    }
}
复制代码

```

visualvm 显示一切波澜不惊, CPU 毫无压力 :

![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2019/11/20/16e870101c1fb71f~tplv-t2oaga2asx-watermark.awebp) ![](images/16e8701028c9f946~tplv-t2oaga2asx-watermark.awebp)

#### 发生死锁的线程

再来看看 blocked 的线程是否消耗 CPU. 而且我们这次玩大一点, 看看出现了死锁的话, 会不会造成 CPU 飙高.(死锁就是两个线程互相 block 对方)

死锁代码如下:

```
package com.test;

public class DeadLock {

    static Object lock1 = new Object();
    static Object lock2 = new Object();

    public static class Task1 implements Runnable {

        @Override
        public void run() {
            synchronized (lock1) {
                System.out.println(Thread.currentThread().getName() + " 获得了第一把锁!!");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println(Thread.currentThread().getName() + " 获得了第二把锁!!");
                }
            }
        }
    }

    public static class Task2 implements Runnable {

        @Override
        public void run() {
            synchronized (lock2) {
                System.out.println(Thread.currentThread().getName() + " 获得了第二把锁!!");
                synchronized (lock1) {
                    System.out.println(Thread.currentThread().getName() + " 获得了第一把锁!!");
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new Task1(), "task-1");
        Thread thread2 = new Thread(new Task2(), "task-2");
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
        System.out.println(Thread.currentThread().getName() + " 执行结束!");
    }
}
复制代码

```

![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2019/11/20/16e87010ef1e6f02~tplv-t2oaga2asx-watermark.awebp) ![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2019/11/20/16e870102633f830~tplv-t2oaga2asx-watermark.awebp) ![](images/16e870104d83259b~tplv-t2oaga2asx-watermark.awebp)

也是可以看到虽然 visualVm 能检测到了死锁, 但是整个 JVM 消耗的 CPU 并没有什么大的起伏的. 也就是说就算是出现了死锁, 理论上也不会影响到系统 CPU.

当然, 虽然死锁不会影响到 CPU, 但是一个系统的资源并不只有 CPU 这一种, 死锁的出现还是有可能导致某种资源的耗尽, 而最终导致服务不可用, 所以死锁还是要避免的.

#### 频繁切换线程上下文的场景

最后, 来看看大量线程切换是否会影响到 JVM 的 CPU.

我们先生成数 2000 个线程, 利用 jdk 提供的 LockSupport.park() 不断挂起这些线程. 再使用 LockSupport.unpark(t) 不断地唤醒这些线程. 唤醒之后又立马挂起. 以此达到不断切换线程的目的.

代码如下:

```
package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

public class TestCpu {
    public static void main(String[] args) {
        int  threadCount = 2000;
        if(args.length > 0){
            threadCount = Integer.parseInt(args[0].trim());
        }
        final List<Thread> list = new ArrayList<>(threadCount);

        // 启动threadCount个线程, 不断地park/unpark, 来表示线程间的切换
        for(int i =0; i<threadCount; i++){
            Thread thread = new Thread(()->{
                while(true){
                    LockSupport.park();
                    System.out.println(Thread.currentThread() +" was unpark");
                }
            });
            thread.setName("cpuThread" + i);
            list.add(thread);
            thread.start();
        }

        // 随机地unpark某个线程
        while(true){
            int i = new Random().nextInt(threadCount);
            Thread t = list.get(i);
            if(t != null){
                LockSupport.unpark(t);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
            }
        }
    }
}

复制代码

```

再观察 visualVm, 发现整个 JVM 的 CPU 的确开始升高了, 但是具体到线程级别, 会发现每个线程都基本不耗 CPU. 说明 CPU 不是这些线程本身消耗的. 而是系统在进行线程上下文切换时消耗的:

jvm 的 cpu 情况:

![](images/16e8701052af49b5~tplv-t2oaga2asx-watermark.awebp)

每个线程的占用 cpu 情况:

![](images/16e8701054a542f4~tplv-t2oaga2asx-watermark.awebp)

### 分析和总结

再回到我们文章开头的线程堆栈 (占用了 15% 的 CPU):

```
"operate-api-1-thread-6" #1522 prio=5 os_prio=0 tid=0x00007f4b7006f800 nid=0x1b67c waiting on condition [0x00007f4ac8c4a000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000006c10828c8> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.parkAndCheckInterrupt(AbstractQueuedSynchronizer.java:836)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued(AbstractQueuedSynchronizer.java:870)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:1199)
	at java.util.concurrent.locks.ReentrantLock$NonfairSync.lock(ReentrantLock.java:209)
	at java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:285)
	at ch.qos.logback.core.OutputStreamAppender.subAppend(OutputStreamAppender.java:210)
	at ch.qos.logback.core.rolling.RollingFileAppender.subAppend(RollingFileAppender.java:235)
	at ch.qos.logback.core.OutputStreamAppender.append(OutputStreamAppender.java:100)
	at ch.qos.logback.core.UnsynchronizedAppenderBase.doAppend(UnsynchronizedAppenderBase.java:84)
	at ch.qos.logback.core.spi.AppenderAttachableImpl.appendLoopOnAppenders(AppenderAttachableImpl.java:51)
	at ch.qos.logback.classic.Logger.appendLoopOnAppenders(Logger.java:270)
	at ch.qos.logback.classic.Logger.callAppenders(Logger.java:257)
	at ch.qos.logback.classic.Logger.buildLoggingEventAndAppend(Logger.java:421)
	at ch.qos.logback.classic.Logger.filterAndLog_0_Or3Plus(Logger.java:383)
	at ch.qos.logback.classic.Logger.info(Logger.java:579)
	...
复制代码

```

上面论证过了, WAITING 状态的线程是不会消耗 CPU 的, 所以这里的 CPU 肯定不是挂起后消耗的, 而是挂起前消耗的.

那是哪段代码消耗的呢? 答案就在堆栈中的这段代码:

```
at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:1199)
复制代码

```

众所周知, ReentrantLock 的底层是使用 AQS 框架实现的. AQS 大家可能都比较熟悉, 如果不熟悉的话这里可以大概描述一下 AQS:

```
1. AQS有个临界变量state,当一个线程获取到state==0时, 表示这个线程进入了临界代码(获取到锁), 并原子地把这个变量值+1
2. 没能进入临界区(获取锁失败)的线程, 会利用CAS的方式添加到到CLH队列尾去, 并被LockSupport.park挂起.
3. 当线程释放锁的时候, 会唤醒head节点的下一个需要唤醒的线程(有些线程cancel了就不需要唤醒了)
4. 被唤醒的线程检查一下自己的前置节点是不是head节点(CLH队列的head节点就是之前拿到锁的线程节点)的下一个节点,
如果不是则继续挂起, 如果是的话, 与其他线程重新争夺临界变量,即重复第1步
复制代码

```

#### CAS

在 AQS 的第 2 步中, 如果竞争锁失败的话, 是会使用 CAS 乐观锁的方式添加到队列尾的, 核心代码如下:

```
   /**
     * Inserts node into queue, initializing if necessary. See picture above.
     * @param node the node to insert
     * @return node's predecessor
     */
    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
复制代码

```

看上面的这段代码, 设想在极端情况下 (并发量非常高的情况下), 每一次执行 compareAndSetTail 都失败(即返回 false) 的话, 那么这段代码就相当是一个 while(true)死循环.

在我们的实际案例中, 虽然不是极端情况, 但是并发量也是极高的 (每一个线程每时每刻都在调用 logback 打日志), 所以在某些情况下, 个别线程会在这段代码自旋过久而长期占用 CPU, 最终导致 CPU 告警

> CAS 也是一种乐观锁, 所谓乐观就是认为竞争情况比较少出现. 所以 CAS 是不适合用于锁竞争严重的场景下的, 锁竞争严重的场景更适合使用悲观锁, 那样线程被挂起了, 会更加节省 CPU

#### AQS 中线程上下文切换

在实际的环境中, 如果临界区的代码执行时间比较短的话 (logback 写日志够短了吧), 上面 AQS 的第 3, 第 4 步也是会导致 CLH 队列的线程被频繁唤醒, 而又由于抢占锁失败频繁地被挂起. 因此也会带来大量的上下文切换, 消耗系统的 cpu 资源.

从实验结果来看, 我觉得这个原因的可能性更高.

### 延伸思考

所谓 cpu 偏高就是指 "cpu 使用率" 过高. 举例说 1 个核的机器, CPU 使用 100%, 8 个核使用了 800%, 都表示 cpu 被用满了. 那么 1 核的 90%, 8 核的 700% 都可以认为 cpu 使用率过高了.

cpu 被用满的后果就是操作系统的其他任务无法抢占到 CPU 资源. 在 window 上的体现就是卡顿, 鼠标移动非常不流畅. 在服务器端的体现就是整个 JVM 无法接受新的请求, 当前的处理逻辑也无法进行而导致超时, 对外的表现就是整个系统不可用.

```
CPU% = (1 - idleTime / sysTime) * 100

idleTime: CPU空闲时间
sysTime: CPU在用户态和内核态的使用时间之和
复制代码

```

> cpu 是基于时间片调度的. 理论上不管一个线程处理时间有多长, 它能运行的时间也就是一个时间片的时间, 处理完后就得释放 cpu. 然而它释放了 CPU 后, 还是会立马又去抢占 cpu, 而且抢到的概率是一样的. 所以从应用层面看, 有时还是可以看到这个线程是占用 100% 的

最后, 从经验来看, 一个 JVM 系统的 CPU 偏高一般就是以下几个原因:

1.  代码中存在死循环
2.  JVM 频繁 GC
3.  加密和解密的逻辑
4.  正则表达式的处理
5.  频繁地线程上下文切换

如果真的遇到了线上环境 cpu 偏高的问题, 不妨先从这几个角度进行分析.

最最最后, 给大家推荐一个工具, 可以线上分析 jstack 的一个网站, 非常的有用.

网站地址: [fastthread.io/](https://link.juejin.cn/?target=https%3A%2F%2Ffastthread.io%2F "https://fastthread.io/")