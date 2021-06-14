package com.example.codeexp2021app.api.model.login;

public class LoginResult {

    private String username;

    public LoginResult() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "LoginResult{" + "username='" + username + '\'' + '}';
    }
}
