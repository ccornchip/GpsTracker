package com.example.gpstracker;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.function.Consumer;

public class GpsForegroundService extends Service {
    public static final String CHANNEL_ID = "CHANNEL_ID";

    private boolean isRunning = false;
    private Consumer<Boolean> isRunningListener;

    public class GpsBinder extends Binder {
        public void setIsRunningListener(Consumer<Boolean> isRunningListener) {
            GpsForegroundService.this.isRunningListener = isRunningListener;
            if (isRunningListener != null) isRunningListener.accept(isRunning);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new GpsBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Energyk")
                .setContentText("We're watching you")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(1, builder.build());
        isRunning = true;
        if (isRunningListener != null) isRunningListener.accept(true);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (isRunningListener != null) isRunningListener.accept(false);
    }
}