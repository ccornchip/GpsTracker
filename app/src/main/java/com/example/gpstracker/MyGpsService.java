package com.example.gpstracker;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MyGpsService extends Service {
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int response = super.onStartCommand(intent, flags, startId);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        return response;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Toast.makeText(MyGpsService.this, "GPS disabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            long time = System.currentTimeMillis();
            String json = "{\"timestamp\":" + time +
                    ",\"latitude\":" + latitude +
                    ",\"longitude\":" + longitude + "}";
            System.out.println(json);
        }
    };

}