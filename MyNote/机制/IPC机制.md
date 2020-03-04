

[toc]



# Android 中的多进程模式

## 多进程的运行机制

在不同进程中的四大组件会拥有独立的虚拟机，Application 和内存空间。



# IPC 基础概念

## Serializable

* Serializable 通过 IO 流的形式将数据从磁盘中读\写，从而实现序列化。所有可以实现数据的持久化保存。
* ==静态成员变量属于类不属于对象，所以不会参与序列化过程==
* transient 关键字标识的成员变量不参与序列化过程。
* serialVersionUID ：序列化后的数据中的 serialVersionUID 只有和当前类的 serialVersionUID 相同才能够正常地被反序列化。具体参见 《Android 开发艺术探究》

```java
    /**
     * 序列化
     */
    public static void serialization() {
        SerializableeDemo demo = new SerializableeDemo();
        try {
            // 将 object 写入一个文件
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("create.txt"));
            stream.writeObject(demo);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /**
     * 反序列化
     */
    public static SerializableeDemo  deserialization() {
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream("create.txt"));
            return (SerializableeDemo) stream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
```



## Parcelable

* 基于内存完成的序列化，相比 Serializable 性能更好，但是如果想持久化到文件会比较复杂，此时可选用 Serializable。



## Parcelable VS. Serializable

1. 在内存的使用中,前者在性能方面要强于后者

2. 后者在序列化操作的时候会产生大量的临时变量,(原因是使用了反射机制)从而导致GC的频繁调用,因此在性能上会稍微逊色

3. Parcelable是以Ibinder作为信息载体的.在内存上的开销比较小,因此在内存之间进行数据传递的时候,Android推荐使用Parcelable,既然是内存方面比价有优势,那么自然就要优先选择.

4. 在读写数据的时候,Parcelable是在内存中直接进行读写,而Serializable是通过使用IO流的形式将数据读写入在硬盘上.

  但是：虽然Parcelable的性能要强于Serializable,但是仍然有特殊的情况需要使用Serializable,而不去使用Parcelable,因为Parcelable无法将数据进行持久化,因此在将数据保存在磁盘的时候,仍然需要使用后者,因为前者无法很好的将数据进行持久化.(原因是在不同的Android版本当中,Parcelable可能会不同,因此数据的持久化方面仍然是使用Serializable)

# Binder

Binder 是 Andoid 中的一个类，他实现了 IBinder 接口，从 IPC 角度来说，IBinder 是 Android 中的一种跨进程通信方式，Binder 可以理解为一种虚拟设备，它的设备驱动是 /dev/binder，该通讯方式在 Linux 中没有。

从 Android Framework 角度来说，Binder 是 ServiceManager 链接各种 Manager (ActivityManager、WindowManager,等等)和相应 ManagerService的桥梁。从 Android 应用层来说，Binder 是客户端和服务端进行通信的媒介。



Binder 是 Android 提供的一种进程间通信机制，它是整个 Android 系统的核心，Android 能进行如此丰富自由的多进程开发也多基于 Binder 机制，一句话，“无 Binder 不 Android”。

但是 Binder 却很难搞明白，复杂程度也远远不是几篇文章就能说清楚的。

我们先从背景入手，理解 Binder 的地位和不可替代性。 --> 再站在高处俯瞰 Binder 的设计，形成一个完整的概念。 --> 再深入理解 Binder 实现原理。

## 背景

- [为什么 Android 要采用 Binder 作为 IPC 机制？](https://www.zhihu.com/question/39440766)

  > Android 的内核基于 Linux，在 Linux 中进程间通讯的方式有很多种：管道、FIFO、消息队列、信号量、共享内存及 Socket（Linux进程间通信），为什么不直接采用 Linux 现有的 IPC 方案？看完这个回答你就有了答案。

## 应用层

- [Binder学习指南](http://weishu.me/2016/01/12/binder-index-for-newer/)

  > 非常详细的一份学习指南，讲解也通俗易懂，适合对Binder一无所知或不知道如何入手学习Binder的小伙伴阅读。

- [写给 Android 应用工程师的 Binder 原理剖析](https://juejin.im/post/5acccf845188255c3201100f)

  > 图文并茂，写得通俗易懂，可以说是初步掌握Binder的必读博文

- [Android Bander设计与实现 - 设计篇](https://blog.csdn.net/universus/article/details/6211589)

  > Binder的文章有很多，但多数是从源码角度去分析。源码分析有一个问题就是，细节太多了，特别初接触Binder的话，是很难聚焦到某个具体问题上的，导致很难总结出Binder这套IPC的设计理念和核心结构。所以我强烈推荐这篇文章，以一种宏观的角度解释了Android系统中的Binder机制。八年过去了，我觉得至今有很多文章都赶不上这篇旧文。

## 底层

- [Android跨进程通信IPC系列文](https://www.jianshu.com/p/36b488863bc0)

  > Binder系列文，由浅入深，方方面面。

- [一篇文章了解相见恨晚的 Android Binder 进程间通讯机制](https://blog.csdn.net/freekiteyu/article/details/70082302)

  > 图画得相当不错。

- [Android进程间通信（IPC）机制Binder简要介绍和学习计划](https://blog.csdn.net/luoshengyang/article/details/6618363)

  > 老罗的系列文章从系统源码角度深入分析了Binder的实现细节，具有很大的参考意义。

- [gityuan Binder系列](http://gityuan.com/2015/10/31/binder-prepare/)

  > 基于Android 6.0的源码剖析。

- [wangkuiwu Binder机制(一) Binder的设计和框架](http://wangkuiwu.github.io/2014/09/01/Binder-Introduce/#anchor2_2_2)

  > 比较老的博文了，胜在图画得非常不错，可以做个参考。

## 最后

- [听说你Binder机制学的不错，来面试下这几个问题](https://www.jianshu.com/p/adaa1a39a274)

  > 挖掘出Binder一些难以理解或细节的点。

## 后记

Binder 机制我觉得非常有趣，为什么呢？

1. 与其它 IPC 不同，Binder 机制用**面向对象的思想**来做设计，Binder 是一个实体位于 Server，引用位于 Client 的对象，该对象提供了一套方法用以实现对 Server 的请求，就象类的成员函数。那在 Client 看来，通过 Binder 引用调用其提供的方法和通过指针调用其它任何本地对象的方法并无区别。**就好像协程的魅力，让你可以用同步的方式写异步代码，Binder 让你用调本地方法的方式调远程服务。**
2. **MMAP 的改造**，MMAP 通常用在有物理介质的文件系统上，实现磁盘与用户空间的映射。而 Binder 并不存在物理介质，却能巧妙使用 MMAP 在内核空间创建数据接收的缓存空间，将内核空间与接收方的用户空间进行内存映射。

所以你说，妙不妙？



# 资料

[!!! 极力推荐 《Binder学习指南》](http://weishu.me/2016/01/12/binder-index-for-newer/)

[Binder系列](http://gityuan.com/2015/10/31/binder-prepare/)

[彻底理解Android Binder通信架构](http://gityuan.com/2016/09/04/binder-start-service/)

