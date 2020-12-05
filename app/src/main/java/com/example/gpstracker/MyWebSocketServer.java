package com.example.gpstracker;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Timer;

public class MyWebSocketServer extends WebSocketServer {

    private Timer timer;

    public MyWebSocketServer(InetSocketAddress inetSocketAddress){
        super(inetSocketAddress);
    }

    @Override
    public void onStart() {
        System.out.println("Start");
        timer = new Timer();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Open");
        timer.scheduleAtFixedRate(new MyTimerTask(conn), 0, 1000);
        System.out.println("Sending");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message : " + message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Close");
        timer.cancel();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("Error");
        ex.printStackTrace();
    }


}
