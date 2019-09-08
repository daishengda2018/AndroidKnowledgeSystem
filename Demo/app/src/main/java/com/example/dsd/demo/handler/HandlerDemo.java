package com.example.dsd.demo.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Android 消息机制 —— Handler
 * <p>
 * Created by im_dsd on 2019-09-07
 */
public class HandlerDemo {

    public static final String TAG = "HandlerDemo";
    private Handler mHandler;
    private Looper mLooper;

    /**
     * 如何在子线程中开启 Handler
     */
    public void startThreadHandler() {
        new Thread("Thread#1") {
            @Override
            public void run() {
                // 手动生成为当前线程生成 Looper
                Looper.prepare();
                mLooper = Looper.myLooper();
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Log.d(TAG,Thread.currentThread().getName() + "  " + msg.what);
                    }
                };
                Log.d(TAG,Thread.currentThread().getName() + "loop 开始 会执行吗？  ");
                // 手动开启循环
                Looper.loop();
                Log.d(TAG,Thread.currentThread().getName() + "loop 结束 会执行吗？  ");
            }

        }.start();

        // 等待线程启动
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"start send message");
        mHandler.sendEmptyMessage(1);
        mHandler.post(() -> Log.d(TAG,Thread.currentThread().getName()));
        // 尝试 1秒 后停止
        try {
            Thread.sleep(1000);
            mLooper.quit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
