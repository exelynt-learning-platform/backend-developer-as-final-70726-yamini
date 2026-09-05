package com.example.demo.dto.auth;

public class LoginResponse {

    private String message;
    private String email;
    private String token;
    private String tokenType = "Bearer";
    private String role;

    public LoginResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "LoginResponse [message=" + message
                + ", email=" + email
                + ", tokenType=" + tokenType
                + ", role=" + role + "]";
    }
}