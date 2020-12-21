> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 [blog.csdn.net](https://blog.csdn.net/songzi1228/article/details/102506136)

0、相关文章：
=======

[Java 中如何保证线程安全性](https://blog.csdn.net/weixin_40459875/article/details/80290875)（1.8w 阅读量，12 赞）

[5、并发编程的 3 个概念：原子性、可见性、有序性](https://blog.csdn.net/u010796790/article/details/52155664)（5k 阅读量，4 赞）

[Java 并发编程：volatile 关键字解析](https://www.cnblogs.com/dolphin0520/p/3920373.html)

1、线程安全在三个方面体现
=============

1.1、原子性：
--------

提供互斥访问，同一时刻只能有一个线程对数据进行操作（atomic、synchronized）；

1.2、可见性：
--------

一个线程对主内存的修改可以及时地被其他线程看到（synchronized、volatile）；

1.3、. 有序性：
----------

一个线程观察其他线程中的指令执行顺序，由于指令重排序，该观察结果一般杂乱无序，（happens-before 原则）。

2、原子性
=====

原子性的实现有两种方式：atomic 类和 synchronized。

2.1、JDK 里面提供了很多 atomic 类，AtomicInteger,AtomicLong,AtomicBoolean 等等，它们是通过 CAS 完成原子性。

2.2、synchronized 是一种同步锁，通过锁实现原子操作。

JDK 提供锁分两种：一种是 synchronized，依赖 JVM 实现锁，因此在这个关键字作用对象的作用范围内是同一时刻只能有一个线程进行操作；另一种是 LOCK，是 JDK 提供的代码层面的锁，依赖 CPU 指令，代表性的是 ReentrantLock。

synchronized 修饰的对象有四种：

*   （1）修饰代码块，作用于调用的对象；
*   （2）修饰方法，作用于调用的对象；
*   （3）修饰静态方法，作用于所有对象；
*   （4）修饰类，作用于所有对象。

3、可见性
=====

对于可见性，JVM 提供了 synchronized 和 volatile。

4、有序性
=====

有序性是指，在 JMM 中，允许编译器和处理器对指令进行重排序，但是重排序过程不会影响到单线程程序的执行，却会影响到多线程并发执行的正确性。

可以通过 volatile、synchronized、lock 保证有序性。