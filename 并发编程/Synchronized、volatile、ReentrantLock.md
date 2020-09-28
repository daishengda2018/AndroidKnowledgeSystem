[TOC]

# Synchronized 关键字

关于 Synchronized 的原理可以参见 [聊聊并发（二）Java SE1.6中的Synchronized](http://ifeve.com/java-synchronized/)

## 概念

* 首先要强调的一点 Synchronized 并不是锁它只是一个关键字，他的作用是标记方法或者代码块是同步的。Synchronized 关键字会直接锁住公共内存，保证资源、数据的同步避免竞争。

* 本质上 Synchronized 锁住的是一个叫 Monitor 的东西，如果一个 Monitor 被 Synchronized 标记，所有访问此 Monitor 的动作都会被阻塞直到 Monitor 被释放掉。

* 被 Synchronized 标记的代码块执行完毕后会自动释放 Monitor 即解除锁。
* Synchronzied 是一个很重的方法。



## Synchronized 大概可以分为以下几种同步块:



### 标记实例方法：实例同步方法

在方法声明的时候加上 Synchronized 关键字，这将告诉 Java 此方法是同步的，此时 Synchronized 的 Monitor 是拥有该方法的对象。所以当前线程可以访问**此实例对象**的任何方法， 而其他线程需要等待  methodA() 结束。

这样每一个 methodA() 方法都同步在不同的对象上，即该方法所属的实例。如果有多个实例存在，那么一个线程一次可以在一个实例同步块中执行，即一个实例一个线程。

```java
public class SynchronizedDemo {  
	/**
   * 此时 synchronized 锁住的是当前对象！
   */
  public synchronized void methodA() {
		……
  }
}
```

### 标记静态方法 ：静态同步方法

静态方法同步和实例方法同步方法一样，也使用synchronized 关键字。被 static 标记的方法也称类方法，他是属于类的而非具体对象，所以如果 Synchronized 方法标记了静态方法，此时的 Monitor 将会是 SynchronizedDemo 的字节码文件（ SynchronizedDemo.class 也就是类对象），在 Java 虚拟机中一个类只有一个类对象，这意味着不管同一个类中的哪个静态同步方法被访问，其他的线程都需等待。即一个字节码文件一个线程。

 对于不同类的静态同步方法，他们的 Monitoer 为各自的字节码文件，所以一个线程可以同时访问不同类中的竟然方法而无需等待。

```java
public class SynchronizedDemo {   
		/**
     * staticA 的写法等价于 staticB
     */
    public static synchronized void staticA() {

    }

    public static void staticB() {
        synchronized (SynchronizedDemo.class){

        }
    }
}
```

### 实例方法中标记代码块


  ```java
      // 此方式将会锁住当前实例
        synchronized (this) {

        }

        // 锁住字节码文件，意味着 SynchronizedDemo 的所有实例都会被锁住
        synchronized (SynchronizedDemo.class) {

        }

        // 锁住指定的实例，这种方式一般用于单独判读
        synchronized (monitor) {

        }
  ```

### 静态方法中标记代码块

```java
public class SynchronizedDemo {   
		/**
     * staticA 、staticB 写法等价
     */
    public static synchronized void staticA() {

    }

    public static void staticB() {
        synchronized (SynchronizedDemo.class){

        }
    }
}
```



# volatile

volatile 在 java 中有两条语意：

1. 保证修饰变量的可见性（但并不能保证修饰变量的原子性）。
2. 禁止指令重排。

## 可见性

vloatile 实现的原理是基于硬件实现的：当变量在线程 A 的工作内存发生改变的时候，会立即回写主存，此时其他线程会嗅探到总线中数据的变化，从而将其他线程工作内存中的变量值标记为无效状态，从而达到写操作先发生于读操作，保证了变量值的可见性。

但是 volatile 并不能保证修饰变量的原子性：例如以下的程序使用 vlatile 关键字修饰了 race ，按照我们的期望一共20个线程每个线程累加一万次，结果应该等于 20w，但是运行的结果只会比 20W 小上不少。

```java
/**
 * 在我的机器上不知道为什么，直接 run main 方法用则程序永远不会停止
 * 而是用 debug 方式运行，着很快得到结果
 * 我的机器配置：
 *  JDK: 1.8_121
 *  CPU: i7 8705G 4核8线程
 *  Mem：32G
 *  OS： macOs 10.15.16
 * Create by im_dsd 2020/9/21 12:05 上午
 */
class VolatileDemo {
    public static volatile int race = 0;

    public static void increase() {
        race++;
    }

    private static final int THREAD_COUNT = 20;

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        increase();
                    }
                }
            });
            threads[i].start();
        }

        while (Thread.activeCount() > 1) {
            Thread.yield();
        }
        System.out.println("race=" + race);
    }
}
```

这是因为 `race++` 并不具有原子性，此操作要分三个步骤完成

1. 线程从主内存中获取 race 的值到工作线程
2. 对获取到的值做 +1操作
3. 将结果写回主内存

所以虽然使用 volatile 修饰，但是还是会发生结果变小的问题：

| 时间 | 线程A                           | 线程B                   |
| ---- | ------------------------------- | ----------------------- |
| T1   | 从主存获取到了 race = 1         |                         |
| T2   | race +1 操作                    |                         |
| T3   |                                 | 从主存获取到了 race = 1 |
| T4   |                                 | race +1 操作            |
| T5   |                                 | 回写主存 race = 2       |
| T6   | 回写主存 race = 2（结果变小了） |                         |

## 禁止重排

java 为了提高运行效率会在编译期对指令进行重排。例如普通的变量仅能保证在方法执行的过程中所有依赖赋值结果的地方都能获取到正确的结果，但是并不能保证变量赋值操作的顺序和程序代码中的执行顺序一致。

最贴近生产开发的莫过于使用 DCL （双重检测）的单例模式了（在 JDK 1.5 开始 volatile 的禁止重排才完全支持，之前的 DCL 都存在缺陷）

```java
class Singleton {
    // 禁止指令重排
    private static volatile Singleton sInstance;

    private Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if (sInstance == null) {
            synchronized (Singleton.class) {
                if (sInstance == null) {
                    sInstance = new Singleton();
                }
            }
        }
        return sInstance;
    }
}
```

具体的解读参见： [单例：双重检验与并发](单例：双重检验与并发.md) 





# 可重入锁：ReentrantLock



