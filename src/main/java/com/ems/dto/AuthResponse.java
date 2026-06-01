package com.ems.dto;

public class AuthResponse {
    private String token;
    private String message;
    private boolean requiresOtp;
    private Integer userId;

    public AuthResponse() {}
    public AuthResponse(String token, String message, boolean requiresOtp) {
        this.token = token;
        this.message = message;
        this.requiresOtp = requiresOtp;
    }
    public AuthResponse(String token, String message, boolean requiresOtp, Integer userId) {
        this.token = token;
        this.message = message;
        this.requiresOtp = requiresOtp;
        this.userId = userId;
    }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRequiresOtp() { return requiresOtp; }
    public void setRequiresOtp(boolean requiresOtp) { this.requiresOtp = requiresOtp; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
