# 线程的分类

主线程

Binder 线程

后台线程

详情参见 《Android 高级进阶》13.1 节末尾第 146 页。

# Android 中的线程分类

为啥更新UI必须在主线程：因为所有的视图控件都不是线程安全的。

## AsyncTask

内部使用 Handler + ThreadPool 完成的。关键的点就是**必须要在主线程中启动, 一个实例只能执行一次任务**。在界面销毁的时候一定要手动停止 AsyncTask，否者被持有的对象将不会被 GC。这是因为被线程持有的对象不会被 GC，这是所有线程和线程池的特点。

内部有两个线程池，一个用于调度的 SerialExecutor，一个用户执行任务的 THREAD_POOL_EXECUTOR

## HandlerThread

Handler 与 Thread 结合的产物，使用 HandlerThread#getHandler() 可以把任务发送到子线程中执行，但是要注意**在不使用的时候需要用 HandlerThread#quit HandlerThread#quitSafe 方法退出**。

核心代码：

```java
		@Override
    public void run() {
        mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
          	// 获取一个与当前线程绑定的 looper
            mLooper = Looper.myLooper();
            // 目的是下面的 wait()
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
    }
    
    /**
     * This method returns the Looper associated with this thread. If this thread not been started
     * or for any reason isAlive() returns false, this method will return null. If this thread
     * has been started, this method will block until the looper has been initialized.  
     * @return The looper.
     */
    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }
        
        // If the thread has been started, wait until the looper has been created.
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                   // 如果 mLooper == null 当前线程会被挂起，也就是说如果没有 start 当前线程而是直接调用 getLooper 会被挂起。
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return mLooper;
    }
```

从中可以看出来，HandlerThread 中只有一个消息队列，队列中的消息是顺序执行的，所以 HandlerThread 是线程安全的，但是这样一来影响了一些吞吐量。



## IntentService

执行完毕后会自动停止。它是一个抽象的方法，需要用户继承并手动实现 onHandlerIntent 方法。当 onHandlerIntent 方法执行完最有一个任务时，会调用 stopSelt 停止服务。
