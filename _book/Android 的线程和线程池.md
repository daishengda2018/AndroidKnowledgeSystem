# 主线程和子线程

为啥更新UI必须在主线程：因为所有的视图控件都不是线程安全的。如果是线程安全你的会存在两个问题：

1. 卡顿
2. 开发者时刻要关心线程安全问题，很难用繁琐。

## AsyncTask

内部使用 Handler + ThreadPool 完成的。关键的点就是**必须要在主线程中启动, 一个实例只能执行一次任务**。在界面销毁的时候一定要手动停止 AsyncTask，否者被持有的对象将不会被 GC。这是因为被线程持有的对象不会被 GC，这是所有线程和线程池的特点。

内部有两个线程池，一个用于调度的 SerialExecutor，一个用户执行任务的 THREAD_POOL_EXECUTOR

## HandlerThread

Handler 与 Thread 结合的产物，使用 HandlerThread#getHandler() 可以把任务发送到子线程中执行，但是要注意**在不使用的时候需要用 HandlerThread#quit HandlerThread#quitSafe 方法退出**

## IntentService

执行完毕后会自动停止。它是一个抽象的方法，需要用户继承并手动实现 onHandlerIntent 方法。当 onHandlerIntent 方法执行完最有一个任务时，会调用 stopSelt 停止服务。

