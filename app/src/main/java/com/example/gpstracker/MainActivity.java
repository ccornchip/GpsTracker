package com.example.gpstracker;

import android.Manifest;
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

    private Button mButton;
    private Intent gpsForegroundServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsForegroundServiceIntent = new Intent(this, GpsForegroundService.class);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        textView.setText(getIpAddress());

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(v -> {
            textView.setText(getIpAddress());

            if (mButton.getText().equals(getResources().getString(R.string.start_text))) {
                askPermissionsAndStartService();
            } else {
                stopService(gpsForegroundServiceIntent);
            }
        });
        bindService(gpsForegroundServiceIntent, this, BIND_ABOVE_CLIENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        askPermissionsAndStartService();
    }

    private void askPermissionsAndStartService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startForegroundService(gpsForegroundServiceIntent);
            bindService(gpsForegroundServiceIntent, this, BIND_ABOVE_CLIENT);
        }
    }

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
        GpsForegroundService service = ((GpsForegroundService.GpsBinder) binder).getService();

        service.setIsRunningListener(isRunning ->
                mButton.setText(isRunning ? R.string.stop_text : R.string.start_text)
        );
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}