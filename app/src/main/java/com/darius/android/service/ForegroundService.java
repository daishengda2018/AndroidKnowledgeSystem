package com.darius.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.darius.android.MainActivity;
import com.darius.android.R;

public class ForegroundService extends Service {

    private static final int NOTIFY_ID = 123;

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

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(resultIntent);
        taskStackBuilder.addParentStack(MainActivity.class);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        //显示通知
        notifyMgr.notify(NOTIFY_ID, notification);
        //启动为前台服务
        startForeground(NOTIFY_ID, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
