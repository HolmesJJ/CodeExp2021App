package com.example.codeexp2021app.api.model.google;

public class CreateTokenResult {

    private String token;
    private long expirationTime;

    public CreateTokenResult() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "CreateTokenParameter{" + "token='" + token + '\'' + ", expirationTime='" + expirationTime + '\'' + '}';
    }
}
