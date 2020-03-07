package com.example.dsd.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 使用 Messenger 方式与 service 通信
 * <p>
 * 已经在 manifest 文件中注册
 * <code>
 * <service
 *  android:name=".service.MessengerService"
 *  android:process=":remote" // 注意这个 service 可是在一个单独的进程哦
 *  />
 * </code>
 */
public class MessengerService extends Service {
    private Messenger mMessenger = new Messenger(new MessengerHandler());
    public static final String TAG = MessengerService.class.getName();
    public static final int MSG_FROM_CLIENT = 100;
    public static final int MSG_FROM_SERVICE = 100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    Log.i(TAG, "receive msg from Client " + msg.getData().getString("msg"));
                    replyMsg(msg);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

        private void replyMsg(Message msg) {
            try {
                // reply to 指定的对象是由 client 传递过来的。
                Messenger client = msg.replyTo;
                Message replyMsg = Message.obtain(this, MSG_FROM_SERVICE);
                Bundle bundle = new Bundle();
                bundle.putString("msg", "稍后回复你");
                replyMsg.setData(bundle);
                client.send(replyMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
