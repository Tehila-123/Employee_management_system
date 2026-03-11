package com.ems.dao;

import com.ems.model.OTPToken;
import com.ems.util.DBConnection;
import java.sql.*;
import java.util.Optional;

public class OTPDAOImpl implements OTPDAO {

    @Override
    public void createToken(OTPToken token) throws SQLException {
        String sql = "INSERT INTO otp_tokens (user_id, otp_code, expiry_time) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, token.getOtpCode());
            stmt.setTimestamp(3, token.getExpiryTime());
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<OTPToken> getLatestToken(int userId) throws SQLException {
        String sql = "SELECT * FROM otp_tokens WHERE user_id = ? AND is_used = FALSE ORDER BY expiry_time DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OTPToken token = new OTPToken();
                    token.setOtpId(rs.getInt("otp_id"));
                    token.setUserId(rs.getInt("user_id"));
                    token.setOtpCode(rs.getString("otp_code"));
                    token.setExpiryTime(rs.getTimestamp("expiry_time"));
                    token.setAttempts(rs.getInt("attempts"));
                    token.setUsed(rs.getBoolean("is_used"));
                    return Optional.of(token);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void incrementAttempts(int otpId) throws SQLException {
        String sql = "UPDATE otp_tokens SET attempts = attempts + 1 WHERE otp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, otpId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void markAsUsed(int otpId) throws SQLException {
        String sql = "UPDATE otp_tokens SET is_used = TRUE WHERE otp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, otpId);
            stmt.executeUpdate();
        }
    }
}

