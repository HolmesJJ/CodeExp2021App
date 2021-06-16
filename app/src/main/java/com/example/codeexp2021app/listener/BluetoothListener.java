package com.example.codeexp2021app.listener;

public interface BluetoothListener {
    public void onListening();
    public void onConnecting();
    public void onConnected();
    public void onDisconnected();
    public void onConnectionFailed();
    public void onMessageReceived(String message);
}
