package com.example.gpstracker;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MyWebSocketServer extends WebSocketServer {

    private WebSocket socket = null;

    public MyWebSocketServer(InetSocketAddress inetSocketAddress){
        super(inetSocketAddress);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        socket = conn;
    }

    public void sendMessage(String message) {
        if (socket != null && socket.isOpen()) {
            socket.send(message);
        }
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
