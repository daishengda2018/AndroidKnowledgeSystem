[toc]

# 什么是乐观锁与悲观锁？

## 悲观锁

总是假设最坏的情况，每次读取数据的时候都默认其他线程会修改数据，因此需要进行加锁操作，当其他线层要访问数据时，都需要阻塞挂起。悲观锁的实现：

* 传统的关系型数据库使用这种锁机制，比如行锁，表锁等，读锁，写锁等，都是在做操作之前先上锁；
* Java里面的同步`synchronized`关键字的实现。

## 乐观锁

乐观锁，其实就是一种思想，总是认为不会产生并发问题，每次读取数据的时候都认为其他线程不会修改数据，所以不上锁，但是在更新的时候会判断一下在此期间有没有其他线程修改过数据，乐观锁适用于读取多的操作，这样可以提高程序的吞吐量。实现方式：

* CAS实现：Java中java.util.concurrent.atomic包下面的原子变量使用了乐观锁的一种CAS实现方式，CAS分析看下节。
* 版本号控制：一般是在数据表中加上一个数据版本号version字段，表示数据被修改的次数，当数据被修改时，version值会加一。当线程A要更新数据值时，在读取数据的同时也会读取version值，在提交更新时，若刚才读取到的version值为当前数据库中的version值相等时才更新，否则重试更新操作，直到更新成功

乐观锁适用于读多写少的情况下（多读场景），悲观锁比较适用于写多读少场景。

# 乐观锁的实现方式-CAS（Compare and Swap），CAS（Compare and Swap）实现原理

## 背景

在 JDK 1.5 之前都是使用 synchronized 关键字保证同步，synchronized 保证了无论那个线程持有共享变量的锁，都会采用独占的方式来访问这些变量，导致会存在这些问题：

* 在多线程竞争下，加锁、释放锁会导致那么多的上下文切换和调度延时，引起性能问题
* 如果一个线程持有锁，其他的线程就都会挂起，等待持有锁的线程释放锁
* 如果一个优先级高的线程等待一个优先级低的线程释放锁，会导致优先级倒置，引起性能风险

为了优化悲观锁这些问题，就出现了乐观锁:

> 假设没有并发冲突，每次不加锁操作同一变量，如果有并发冲突导致失败，这重试直至成功。



## CAS（Compare and Swap）原理

CAS：Compare and swap 比较替换。是一种适用于多线程环境下实现同步功能的机制，其也是无所优化，或者叫自旋，还有自适应自旋。

在 JDK 中，CAS 加 volatile 关键字作为实现并发包的基石。没有 CAS 就不再会有并发包，java.util.concurrent 中借助了 CAS 指令实现了一种区别于 synchronized 的一种乐观锁。



乐观锁的一种典型实现机制（CAS），乐观锁主要就是两个步骤：

* 冲突检测
* 数据更新



当多个线程尝试使用 CAS 同时更新一个变量时，只有一个线程可以更新变量值，其他的线程都会失败，失败的线程并不会挂起，而是告知这次竞争中失败了，并可以再次尝试。

**在不使用锁的情况下保证线程安全，CAS实现机制中有重要的三个操作数：**

- **需要读写的内存位置(V)**
- **预期原值(A)**
- **新值(B)**

首先先读取需要读写的内存位置(V)，然后比较需要读写的内存位置(V)和预期原值(A)，如果内存位置与预期原值的A相匹配，那么将内存位置的值更新为新值B。如果内存位置与预期原值的值不匹配，那么处理器不会做任何操作。无论哪种情况，它都会在 CAS 指令之前返回该位置的值。具体可以分成三个步骤：

- **读取（需要读写的内存位置(V)）**
- **比较（需要读写的内存位置(V)和预期原值(A)）**
- **写回（新值(B)）**



==这是一个CPU指令，所有CAS是具有原子性的。==
通常将CAS成为乐观锁（其实根部不是锁），synchronized为悲观锁，因为synchronized会阻塞线程，而CAS不会。

# 工作过程
 主要有三部操作：
1. 获取内存中的元素记为 older
2. 再获取一遍元素值记为 A 与事先获取的 older 比较
3. 如果 A 与 older 相同，则替换为B

# Java中的CAS使用
  java中的CAS主要使用在Atomic* 这一类的原子对象中，以AtomicInteger为例：

## AtomicInteger
```java
  /**
  * Atomically increments by one the current value.
  *
  * @return the updated value
  */
  public final int incrementAndGet() {
  return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
  }
```
可以看到AtomicInteger是使用Unsafe直接操作内存的。

```java
  private static final long valueOffset;
  static {
  	try {
  	  // 而这个 valueOffse 是通过反射直接拿到 value 字段的内存地址，注意：valueOffse 是静态的。
  		valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
  	} catch (Exception ex) { throw new Error(ex); }
   }
```


##   Unsafe#getAndAddInt
```java
  public final int getAndAddInt(Object var1, long var2, int var4) {
  	int var5;
  	do {
  		var5 = this.getIntVolatile(var1, var2);
  	} while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
  		return var5;
  }
```
## Unsafe#compareAndSwapXX 参数解读

在上面的列子中使用到了 UNSAFE 的 compareAndSwapXX 方法

```java
/**
 *@param object 需要更改的对象
 *@param offset 对象在内存中偏移量为offset处的值,就是要修改的数据的值在内存中的偏移量，结合object + offect找到要修改的值
 *@param expect 更新的目标值
 *@param update 对内存中期望的值，如果此值和 object + offect 指向的值一致则更新 object + offect 为 update 值
 */
compareAndSwapInt(Object object, long offset, int expect, int update);
compareAndSwapObject(Object object, long offset, int expect, int update);
```



compareAndSwapInt 是一个Native方法，Java一般情况下不允许用户直接操作系统内存，但是还是通过Unsafe类提供了后门，通过native方法直接可以直接操作内存

compareAndSwapInt  有四个参数，分别代表：对象、对象的地址、预期值、修改值（有位伙伴告诉我他面试的时候就问到这四个变量是啥意思…+_+）。该方法的实现这里就不做详细介绍了，有兴趣的伙伴可以看看openjdk的源码。
CAS可以保证一次的读-改-写操作是原子操作，在单处理器上该操作容易实现，但是在多处理器上实现就有点儿复杂了。
CPU提供了两种方法来实现多处理器的原子操作：总线加锁或者缓存加锁。
### 总线加锁：
总线加锁就是就是使用处理器提供的一个LOCK#信号，当一个处理器在总线上输出此信号时，其他处理器的请求将被阻塞住,那么该处理器可以独占使用共享内存。但是这种处理方式显得有点儿霸道，不厚道，他把CPU和内存之间的通信锁住了，在锁定期间，其他处理器都不能其他内存地址的数据，其开销有点儿大。所以就有了缓存加锁。
### 缓存加锁：
其实针对于上面那种情况我们只需要保证在同一时刻对某个内存地址的操作是原子性的即可。缓存加锁就是缓存在内存区域的数据如果在加锁期间，当它执行锁操作写回内存时，处理器不在输出LOCK#信号，而是修改内部的内存地址，利用缓存一致性协议来保证原子性。缓存一致性机制可以保证同一个内存区域的数据仅能被一个处理器修改，也就是说当CPU1修改缓存行中的i时使用缓存锁定，那么CPU2就不能同时缓存i。

# ABA问题
经典 CAS 是无法解决 ABA 问题。ABA 问题描述的是：设置两个线程分别为 T1、T2，他们同时操作一个元素，一开始 T1 抢占到了 CPU 执行权限，从元素中获取到了值 A，这是 T1 被挂起 T2 开始执行， T2 一开始将元素的值 A 替换成了 B，然后又替换成了 A，此时 T1 被唤醒开始执行，他发现元素的值还是原来 A 则认为比较成功可以重新赋值了，但殊不知此 A 非彼 A。

用生活中小偷调包的例子解释一下，可能会更加清晰：

> 小王带了一个精致的黑色公文箱在候机厅候机，此时一个火辣美女吸引了小王的目光让他忘乎所以，此时一个小偷把小王的公文箱替换成了一个一摸一样的空箱子而小王并不知情，当美女离开后，小王看了看箱子还在安心的继续等待……

# ABA 的解决办法
==使用计数器，判断操作数量前后是否一致，如果不一致说明被修改了==



# 参考

[无锁队列的实现](https://coolshell.cn/articles/8239.html)