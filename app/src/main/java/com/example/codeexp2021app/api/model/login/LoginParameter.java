package com.example.codeexp2021app.api.model.login;

public class LoginParameter {

    private String username;
    private String password;

    public LoginParameter() {
    }

    public String getUsername() {
        return username;
    }

    public LoginParameter setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LoginParameter setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "LoginParameter{" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
    }
}
