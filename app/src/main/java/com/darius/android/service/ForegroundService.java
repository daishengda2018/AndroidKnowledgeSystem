package com.darius.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.darius.android.R;

public class ForegroundService extends Service {
    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
    }

    private void showNotification() {
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this,"default")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("ForegroundService")
            .setContentTitle("ForegroundService");

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
