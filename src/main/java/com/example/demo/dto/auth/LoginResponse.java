package com.example.demo.dto.auth;

public class LoginResponse {

    private String message;
    private String email;
    private String token;

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

	@Override
	public String toString() {
		return "LoginResponse [message=" + message + ", email=" + email + "]";
	}
    
    
}