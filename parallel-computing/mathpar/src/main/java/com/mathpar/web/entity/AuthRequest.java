package com.mathpar.web.entity;

public class AuthRequest {
    public static final String ACT_REGISTER = "register";
    public static final String ACT_LOGIN = "login";
    public static final String ACT_ISLOGGEDIN = "isloggedin";
    public static final String ACT_LOGOUT = "logout";

    private String email;
    private String password;
    private String action;

    public AuthRequest() {
    }

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
