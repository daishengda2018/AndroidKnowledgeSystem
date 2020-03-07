package com.example.dsd.demo.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.dsd.demo.R;

import org.jetbrains.annotations.NotNull;

public class MessengerActivity extends AppCompatActivity {
    // 申明一个用于回复的 messenger
    private Messenger mReplyMessenger = new Messenger(new MessengerHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, createServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    @NotNull
    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    Messenger messenger = new Messenger(service);
                    Message msg = Message.obtain(null, MessengerService.MSG_FROM_CLIENT);
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "哈哈哈哈哈哈哈");
                    // 设置回复的 messenger，在 service 端可以通过 replyTo 拿到它的引用
                    msg.replyTo = mReplyMessenger;
                    msg.setData(bundle);
                    messenger.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerService.MSG_FROM_SERVICE:
                    Log.i(MessengerService.TAG, "receive msg from Service   " + msg.getData().getString("msg"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
