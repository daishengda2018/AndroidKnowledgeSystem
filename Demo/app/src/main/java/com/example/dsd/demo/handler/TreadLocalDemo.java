package com.example.dsd.demo.handler;

/**
 * Handler 消息机制的基石 ：TreadCocal
 * <p>
 * Created by im_dsd on 2019-09-05
 */
public class TreadLocalDemo {
    // 就算设置为 static 结果也是一样的
    ThreadLocal<Boolean> mThreadLocal = new ThreadLocal<Boolean>();

    public void runDemo() {
        mThreadLocal.set(true);
        System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
        new Thread("Thread#1") {
            @Override
            public void run() {
                super.run();
                mThreadLocal.set(false);
                System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
            }
        }.start();

        new Thread("Thread#2") {
            @Override
            public void run() {
                super.run();
                System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
            }
        }.start();
        System.out.println(Thread.currentThread().getName() + "  " + mThreadLocal.get());
    }
}
