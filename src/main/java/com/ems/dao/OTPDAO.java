package com.ems.dao;

import com.ems.model.OTPToken;
import java.sql.SQLException;
import java.util.Optional;

public interface OTPDAO {
    void createToken(OTPToken token) throws SQLException;
    Optional<OTPToken> getLatestToken(int userId) throws SQLException;
    void incrementAttempts(int otpId) throws SQLException;
    void markAsUsed(int otpId) throws SQLException;
}

