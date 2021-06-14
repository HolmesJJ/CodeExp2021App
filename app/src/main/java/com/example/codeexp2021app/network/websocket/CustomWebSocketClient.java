package com.example.codeexp2021app.network.websocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class CustomWebSocketClient extends WebSocketClient {

    private static final String TAG = "CustomWebSocketClient";
    private final WebSocketClientManager webSocketClientManager;

    public CustomWebSocketClient(WebSocketClientManager webSocketClientManager, URI serverURI, Map<String, String> httpHeaders) {
        super(serverURI, httpHeaders);
        this.webSocketClientManager = webSocketClientManager;
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {
        Log.i(TAG, "onOpen");
    }

    @Override
    public void onMessage(String message) {
        Log.i(TAG, "onMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        webSocketClientManager.disconnect();
        Log.i(TAG, "onClose: " + code + ", " + reason + ", " + remote);
    }

    @Override
    public void onError(Exception ex) {
        webSocketClientManager.disconnect();
        Log.i(TAG, "Socket Exception: " + ex.toString());
    }
}
