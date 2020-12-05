package com.example.gpstracker;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MyWebSocketServer extends WebSocketServer {

    public MyWebSocketServer(InetSocketAddress inetSocketAddress){
        super(inetSocketAddress);
    }

    @Override
    public void onStart() {
        System.out.println("Start");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Open");
        conn.send("{\"latitude\":123,\"longitude\":456}");
        System.out.println("Sent");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message : " + message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Close");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("Error");
        ex.printStackTrace();
    }


}
