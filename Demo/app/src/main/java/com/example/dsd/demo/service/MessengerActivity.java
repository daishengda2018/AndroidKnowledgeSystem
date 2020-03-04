package com.example.dsd.demo.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dsd.demo.R;

import org.jetbrains.annotations.NotNull;

public class MessengerActivity extends AppCompatActivity {

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
}
