package com.ems.dto;

public class OtpVerificationRequest {
    private int userId;
    private String code;

    public OtpVerificationRequest() {}
    public OtpVerificationRequest(int userId, String code) {
        this.userId = userId;
        this.code = code;
    }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
