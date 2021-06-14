package com.example.codeexp2021app.network.websocket;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class CustomWebSocketServer extends WebSocketServer {

    private static final String TAG = "CustomWebSocketServer";
    private final WebSocketServerManager webSocketServerManager;

    public CustomWebSocketServer(WebSocketServerManager webSocketServerManager, int port) {
        super(new InetSocketAddress(port));
        this.webSocketServerManager = webSocketServerManager;
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        webSocketServerManager.addWebSocket(conn);
        Log.i(TAG, "Some one Connected...");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        webSocketServerManager.removeWebSocket(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        webSocketServerManager.sendMessage("hello");
        Log.i(TAG, "OnMessage: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        webSocketServerManager.removeWebSocket(conn);
        Log.i(TAG, "Socket Exception: " + ex.toString());
    }

    @Override
    public void onStart() {
        Log.i(TAG, "Start WebSocket Server Success");
    }
}