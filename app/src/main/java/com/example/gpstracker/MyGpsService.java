package com.example.gpstracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MyGpsService extends Service {

    private WebSocketServer wss;

    public MyGpsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int response =  super.onStartCommand(intent, flags, startId);

        int port = 3000;

        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        wss = new MyWebSocketServer(inetSocketAddress);
        wss.start();

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