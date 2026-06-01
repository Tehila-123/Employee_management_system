package com.ems.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "otp_tokens")
public class OTPToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int otpId;

    private int userId;
    private String otpCode;
    private Timestamp expiryTime;
    private int attempts;
    private boolean isUsed;

    public int getOtpId() { return otpId; }
    public void setOtpId(int otpId) { this.otpId = otpId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public Timestamp getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Timestamp expiryTime) { this.expiryTime = expiryTime; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }
}



