package com.example.codeexp2021app.constants;

public enum MessageType {
    RECOGNIZED("Recognized");

    private String message;

    MessageType(String message) {
        this.message = message;
    }

    public String getValue() {
        return message;
    }
}
