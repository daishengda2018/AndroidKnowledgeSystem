package com.example.dsd.demo.concurrency;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 创建 thread 的几种方式
 * <p>
 * Created by im_dsd on 2019-08-29
 */
public class CreateThreadDemo {
    public static void main(String[] args) {
//        thread();
//        runnale();
//        threadAndRunnable();
//        threadFactory();
        executor();
    }

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

    private static void executor() {
        Runnable runnable = () -> System.out.println(Thread.currentThread().getName() + " stared ");
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(runnable);
        // submit 会返回一个 Future 可以获取返回值
        threadPool.submit(runnable);
    }

    private static void callable() {

    }
}
