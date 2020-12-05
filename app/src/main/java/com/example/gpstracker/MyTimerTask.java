package com.example.gpstracker;

import org.java_websocket.WebSocket;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    private final WebSocket mWebSocket;
    private int count = 0;

    public MyTimerTask(WebSocket mWebSocket) {
        this.mWebSocket = mWebSocket;
    }

    @Override
    public void run() {
        count++;
//        mWebSocket.send("{\"latitude\":123,\"longitude\":456}");
        mWebSocket.send(Integer.toString(count));
    }
}
