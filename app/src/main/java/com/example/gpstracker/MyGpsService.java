package com.example.gpstracker;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MyGpsService extends Service {

    private MyWebSocketServer wss;

    public MyGpsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int response = super.onStartCommand(intent, flags, startId);

        int port = 3000;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        wss = new MyWebSocketServer(inetSocketAddress);
        wss.start();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = location -> {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            long time = System.currentTimeMillis();
            String json = "{\"timestamp\":"+time+
                    ",\"latitude\":"+latitude+
                    ",\"longitude\":"+longitude+"}";
            wss.sendMessage(json);
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//               || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 100, 0, locationListener);
        }

        return response;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            wss.stop();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}