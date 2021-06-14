package com.example.codeexp2021app.network.websocket;

import android.util.Log;

import java.net.URI;
import java.util.Map;

public class WebSocketClientManager {

    private static final String TAG = "WebSocketClientManager";
    private CustomWebSocketClient customWebSocketServerClient = null;
    private Boolean isConnected = false;

    public WebSocketClientManager() {

    }

    private static class SingleInstance {
        private static WebSocketClientManager INSTANCE = new WebSocketClientManager();
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static WebSocketClientManager getInstance() {
        return WebSocketClientManager.SingleInstance.INSTANCE;
    }

    public boolean connect(URI serverURI, Map<String, String> httpHeaders) {
        if (!isConnected) {
            try {
                Log.i(TAG, serverURI.getPath());
                customWebSocketServerClient = new CustomWebSocketClient(this, serverURI, httpHeaders);
                isConnected = true;
                Log.i(TAG,"Connect WebSocketServer Success...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"Connect WebSocketServer Failed...");
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean connectBlocking() {
        if (isConnected && customWebSocketServerClient != null) {
            try {
                customWebSocketServerClient.connectBlocking();
                Log.i(TAG,"Connect Blocking Success...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"Connect Blocking Failed...");
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean disconnect() {
        if (isConnected && customWebSocketServerClient != null) {
            try {
                customWebSocketServerClient = null;
                isConnected = false;
                Log.i(TAG, "Disconnect WebSocketServer Success...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Disconnect WebSocketServer Failed...");
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isConnected() { return isConnected; }

    public void sendMessage(String message) {
        if(customWebSocketServerClient != null && isConnected) {
            try {
                customWebSocketServerClient.send(message);
                Log.i(TAG, "message: " + message);
            } catch (Exception e) {
                Log.i(TAG, "Send Exception: " + e);
            }
        }
    }
}
