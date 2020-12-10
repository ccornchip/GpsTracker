package com.example.gpstracker;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private static final String SERVICE_RUNNING = "SERVICE_RUNNING";

    private Button mButton;
    private boolean serviceIsStarted = false;
    private Intent gpsForegroundServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsForegroundServiceIntent = new Intent(this, GpsForegroundService.class);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            serviceIsStarted = savedInstanceState.getBoolean(SERVICE_RUNNING);
        }

        TextView textView = findViewById(R.id.textView);
        textView.setText(getIpAddress());

        mButton = findViewById(R.id.button);
//        updateButtonText();
        mButton.setOnClickListener(v -> {
            textView.setText(getIpAddress());

            if (mButton.getText().equals(getResources().getString(R.string.start_text))) {
                askPermissionsAndStartService();
            } else {
                stopService(gpsForegroundServiceIntent);
            }
//            serviceIsStarted = !serviceIsStarted;
//            updateButtonText();
        });

        bindService(gpsForegroundServiceIntent, this, BIND_ABOVE_CLIENT);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        askPermissionsAndStartService();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void askPermissionsAndStartService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
//            startService(new Intent(this, MyGpsService.class));
            startForegroundService(gpsForegroundServiceIntent);
            bindService(gpsForegroundServiceIntent, this, BIND_ABOVE_CLIENT);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SERVICE_RUNNING, serviceIsStarted);
    }

//    private void updateButtonText() {
//        if (!serviceIsStarted) {
//            mButton.setText(R.string.start_text);
//        } else {
//            mButton.setText(R.string.stop_text);
//        }
//    }

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        System.out.println("connected");
        GpsForegroundService service = ((GpsForegroundService.GpsBinder) binder).getService();

        service.setIsRunningListener(isRunnning -> {
            if (isRunnning) {
                mButton.setText(R.string.stop_text);
            } else {
                mButton.setText(R.string.start_text);
            }
        });

        System.out.println();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        System.out.println("disconnected");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}