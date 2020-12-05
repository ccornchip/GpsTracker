package com.example.gpstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.TextView;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private static final String SERVICE_RUNNING = "SERVICE_RUNNING";

    private Button mButton;
    private boolean serviceIsStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            serviceIsStarted = savedInstanceState.getBoolean(SERVICE_RUNNING);
        }

        TextView textView = findViewById(R.id.textView);
        textView.setText(getIpAddress());

        mButton = findViewById(R.id.button);
        updateButtonText();
        Intent intent = new Intent(this, MyGpsService.class);
        mButton.setOnClickListener(v -> {
            textView.setText(getIpAddress());

            if (!serviceIsStarted) {
                startService(intent);
            } else {
                stopService(intent);
            }
            serviceIsStarted = !serviceIsStarted;
            updateButtonText();
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SERVICE_RUNNING, serviceIsStarted);
    }

    private void updateButtonText() {
        if (!serviceIsStarted) {
            mButton.setText(R.string.start_text);
        } else {
            mButton.setText(R.string.stop_text);
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
}