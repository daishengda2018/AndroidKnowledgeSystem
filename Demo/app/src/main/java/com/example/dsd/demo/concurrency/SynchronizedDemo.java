package com.example.dsd.demo.concurrency;

/**
 * synchronized demo
 * <p>
 * Created by im_dsd on 2019-08-29
 */
public class SynchronizedDemo {
    private int x = 0;
    private int y = 0;
    private String name = "";
    private final Object monitor = new Object();


    {
        // 此方式将会锁住当前实例
        synchronized (this) {

        }

        // 锁住字节码文件，意味着 SynchronizedDemo 的所有实例都会被锁住
        synchronized (SynchronizedDemo.class) {

        }

        // 锁住指定的实例，这种方式一般用于单独判读
        synchronized (monitor) {

        }
    }


    /**
     *
     */
    public void count(int value) {
        x = value;
        y = value;
    }


    /**
     * 此时 synchronized 锁住的是当前对象！
     */
    public synchronized void methodA() {

    }

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
