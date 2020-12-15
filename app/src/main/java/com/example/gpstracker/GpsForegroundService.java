package com.example.gpstracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class GpsForegroundService extends Service {
    public static final String CHANNEL_ID = "CHANNEL_ID";

    private LocationManager mLocationManager;
//    private MyWebSocketServer wss;

    private boolean isRunning = false;
    private Consumer<Boolean> isRunningListener;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mRequestQueue = Volley.newRequestQueue(this);
    }

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

        // Prepare foreground service
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Energyk")
                .setContentText("We're watching you")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(1, builder.build());

        // Start the web socket
//        int port = 3000;
//        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
//        wss = new MyWebSocketServer(inetSocketAddress);
//        wss.start();

        // Set the GPS listener
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Notify bound applications that the service is running
        isRunning = true;
        if (isRunningListener != null) isRunningListener.accept(true);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop the web socket
//        try {
//            wss.stop();
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//        }

        // Unset the GPS listener
        mLocationManager.removeUpdates(locationListener);

        // Notify bound applications that the service has stopped
        isRunning = false;
        if (isRunningListener != null) isRunningListener.accept(false);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Toast.makeText(GpsForegroundService.this, "GPS disabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            long time = location.getTime();
            final String json = "{\"timestamp\":" + time / 1000 +
                    ",\"latitude\":" + latitude +
                    ",\"longitude\":" + longitude + "}";
//            wss.sendMessage(json);
            mRequestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            MainActivity.URL,
                            null, null) {
                        @Override
                        public byte[] getBody() {
                            return json.getBytes();
                        }
                    }
            );
        }
    };

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel name";
            String description = "Channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}