package com.example.codeexp2021app.network.websocket;

import android.util.Log;

import org.java_websocket.WebSocket;

public class WebSocketServerManager {

    private static final String TAG = "WebSocketServerManager";
    private CustomWebSocketServer customWebSocketServerServer = null;
    private WebSocket webSocket = null;
    private Boolean isStarted = false;

    public WebSocketServerManager() {

    }

    private static class SingleInstance {
        private static WebSocketServerManager INSTANCE = new WebSocketServerManager();
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static WebSocketServerManager getInstance() {
        return SingleInstance.INSTANCE;
    }

    public void addWebSocket(WebSocket socket){
        if(webSocket != null && !webSocket.isClosed()) {
            webSocket.close();
            webSocket = null;
        }
        webSocket = socket;
        Log.i(TAG, "addWebSocket");
    }

    public void removeWebSocket(WebSocket socket) {
        if(!socket.isClosed()) {
            socket.close();
            socket = null;
        }
        if(!webSocket.isClosed()) {
            webSocket.close();
            webSocket = null;
        }
        Log.i(TAG, "removeWebSocket");
    }

    public void sendMessage(String message) {
        if(webSocket != null && isStarted) {
            try {
                webSocket.send(message);
                Log.i(TAG, "message: " + message);
            } catch (Exception e) {
                Log.i(TAG, "Send Exception: " + e);
            }
        }
    }

    public boolean start() {
        if (!isStarted) {
            try {
                customWebSocketServerServer = new CustomWebSocketServer(this, 50050);
                customWebSocketServerServer.start();
                isStarted = true;
                Log.i(TAG,"Start WebSocketServer Success...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"Start WebSocketServer Failed...");
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean stop() {
        if (isStarted && customWebSocketServerServer != null) {
            try {
                customWebSocketServerServer.stop();
                customWebSocketServerServer = null;
                isStarted = false;
                Log.i(TAG, "Stop WebSocketServer Success...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Stop WebSocketServer Failed...");
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isServerStarted() { return isStarted; }
}
