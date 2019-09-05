

[TOC]

#  Java的对象引用

软引用、弱引用都指定说其应用的对象**只存在软引用或者弱引用**，不存在其他的强引用了这样GC才能回收他们，之前对于引用的理解有偏差，一直错误的认为，只要把引用放到软、弱引用中就会有GC逻辑，不是的，前提必须是不存在强引用。



# 多线程和线程同步

## 进程与线程

* 进程：相对隔离的工作空间，每个进程直接彼此独立。

* 什么是线程：按代码顺序执行下来，执行完毕就结束的一条线。

  * Android 的 UI 线程为啥不结束呢？因为它在初始化的时候就开始执行死循环，什么操作系统把它停止了什么时候才会停止，每一次循环都是一次界面刷新，如果卡时间长了就会出现 ANR问题。

  * CPU 线程：CPU 线程是通过物理手段支持的，简单的来说就是 CPU 可以同时干多少件事情。
  * 操作系统线程：通过时间分片模拟出来的线程，是一种逻辑线程。我们所说的一般都是操作系统线程。通过这种时间分片的模式，哪怕 CPU 仅支持单线程，操作系统也可以模拟出几百个逻辑线程。

## 创建线程的方式

### thread

```java
    /**
     * 通过 thread 直接创建
     */
    private static void thread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                // 需要执行的逻辑
                System.out.println("Thread started");
            }
        };

        // 启动新的线程执行逻辑
        thread.start();
    }
```

### runnable

```java
/**
 * 通过 runnable 的方式
 */
private static void runnale() {
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            System.out.println("Thread started");
        }
    };

    Thread thread = new Thread(runnable);
    thread.start();
}
```

### threadAndRunnable

```java
    /**
     * 此方式两个 run 方法都会执行，
     */
    private static void threadAndRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread started 1");
            }
        };

        Thread thread = new Thread(runnable) {
            @Override
            public void run() {
                // super.run() 不能少，否者 Runnable 不能执行
                super.run();
                System.out.println("Thread started 2");
            }
        };
        thread.start();
    }
```

### threadFactory

```java
/**
     * 通过工厂方法生产 thread
     */
    private static void threadFactory() {
        // 一个简单的工厂方法，用于生产 thread
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r);
            }
        };
        Runnable runnable = () -> System.out.println(Thread.currentThread().getName() + " stared ");

        Thread thread1 = threadFactory.newThread(runnable);
        thread1.start();
        Thread thread2 = threadFactory.newThread(runnable);
        thread2.start();
    }
```

### execute

```java
   private static void executor() {
        Runnable runnable = () -> System.out.println(Thread.currentThread().getName() + " stared ");
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(runnable);
        // submit 会返回一个 Future 可以获取返回值
        threadPool.submit(runnable);
    }
```

### callback

```java
    private static void callable() {
        Callable<String> callback = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    System.out.println("started");
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Done!";
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(callback);

        try {
            // future.get() 方法会阻塞线程，可以使用 future.isDone(）+ while 的形式判断。
            String result = future.get();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 循环判断是否结束
//        while (!future.isDone()) {
//        }
    }
```

## 停止线程

### Thrad#stop()

stop 方法已经被弃用了，原因是 stop 方法过于霸道，调用 stop 后立即终止线程内执行的逻辑，导致整个过程不可控，出现意想不到的异常。

```java
    private static void thread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                // 需要执行的逻辑
                System.out.println("Thread started");
                for (int i = 0; i < 1_000_000; i++) {
                    System.out.println("number  " + i);
                }
            }
        };

        // 启动新的线程执行逻辑
        thread.start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 立即停止线程
        thread.stop();
    }
```



### Thrad#interrupt()

interrupt 的本意是打断、中断的意思，此方法仅仅是给线程加了一个标记，并没有停止线程。开发者可以在线程内通过 `isInterrupted()` 判断当前线程的状态，做出响应。

```java
    private static void thread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                // 需要执行的逻辑
                System.out.println("Thread started");
                for (int i = 0; i < 1_000_000; i++) {
                    // 根据标记操作逻辑
                    if (isInterrupted()) {
                        break;
                    }
                    System.out.println("number  " + i);
                }
            }
        };

        // 启动新的线程执行逻辑
        thread.start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 为线程加上标记
        thread.interrupt();
    }
```

除了`isInterrupted()` 方法外 `Thread.interrupted()`也有一样的功能，而且调用后会重置 interrupt 的状态。可以下次再次使用此线程。
```java
// 在判断后将 interrupt 重置
if (Thread.interrupted()) {
	break;
}
```



### InterruptedException

当阻塞方法收到中断请求(`interrupt()`调用的时候)的时候就会抛出InterruptedException异常。

例如：在当前线程 sleep 的这一秒过程中调用 `interrupt()`方法，就会立即停止 sleep 并抛出异常。**InterruptedException 被触发后会和 `Thread.interrupted() ` 一样重置 interrupt 的状态 **

```java
     try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
```

```java
     try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
```

这个知识点能够牵扯出来的内容很多，可以参考[InterruptedException异常处理](https://www.jianshu.com/p/a8abe097d4ed) 这篇文章，具体的内容日后总结。



## 线程同步与线程安全

## 锁

### 什么是死锁

### 读写锁

### 乐观锁、悲观锁

## synchronized 关键字

直接锁住公共内存，

- monitor的概念
- 保证资源、数据的同步

Synchronzied 是一个很重的方法。

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



## volatile



## 线程池

常用线程池



* newSingleThreadExcutor

* newCacheThreadExcutor

* newFixedThreadPool

* newSchudleThreadExcutor

shutdown ： 较柔和，不再让新的任务进入，等待正在执行的任务执行完成

shutdownNow：暴力，立即停止所有的任务。



将线程数和 CPU 数量挂钩：让代码的积极度在不同机器上的表现是一致的。并不是说可以提高 CPU 的利用率。



## 线程间的交互

### Wait & notify

wait 并不是线程的方法，他是 Object 方法，相当于控制的是 monitor。如果没有 monitor 的情况（例如没有 synchronize）使用 wait 方法会直接报错

`notiryAll()` 唤醒所有线程。



### Thread#join()

相当于不需要 synchronized 的 wait 方法。

### Thread.yield()

做出**一下**让步，让步给同优先级的线程。



## 线程池





### 线程造成的内存泄漏

JVM 不会回收正在运行中的线程。

Static 



# HashMap

# concurrentHashMap


