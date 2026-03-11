package com.ems.service;

import com.ems.dao.AuditLogDAO;
import com.ems.dao.OTPDAO;
import com.ems.dao.UserDAO;
import com.ems.model.OTPToken;
import com.ems.model.User;
import com.ems.util.EmailUtil;
import com.ems.util.SecurityUtil;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;
    private final OTPDAO otpDAO;
    private final AuditLogDAO auditLogDAO;
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(AuthService.class.getName());

    public AuthService(UserDAO userDAO, OTPDAO otpDAO, AuditLogDAO auditLogDAO) {
        this.userDAO = userDAO;
        this.otpDAO = otpDAO;
        this.auditLogDAO = auditLogDAO;
    }

    public Optional<User> authenticate(String email, String password, String ipAddress) throws SQLException {
        Optional<User> userOpt = userDAO.getUserByEmail(email);

        if (!userOpt.isPresent()) {
            auditLogDAO.createLog(0, "LOGIN_FAILED", "Invalid email: " + email, ipAddress);
            return Optional.empty();
        }

        User user = userOpt.get();

        if (user.isLocked()) {
            auditLogDAO.createLog(user.getUserId(), "LOGIN_LOCKED", "Locked account attempt", ipAddress);
            return Optional.of(user); // Still return to show locked state
        }

        if (SecurityUtil.checkPassword(password, user.getPasswordHash())) {
            userDAO.resetFailedAttempts(user.getUserId());
            auditLogDAO.createLog(user.getUserId(), "LOGIN_STEP1_SUCCESS", "Password verification successful",
                    ipAddress);
            return Optional.of(user);
        } else {
            int attempts = user.getFailedLoginAttempts() + 1;
            userDAO.updateFailedAttempts(user.getUserId(), attempts);
            if (attempts >= 5) {
                userDAO.lockAccount(user.getUserId());
                auditLogDAO.createLog(user.getUserId(), "ACCOUNT_LOCKED", "5 failed attempts", ipAddress);
            }
            auditLogDAO.createLog(user.getUserId(), "LOGIN_FAILED", "Invalid password", ipAddress);
            return Optional.empty();
        }
    }

    public void generateAndSendOTP(User user) throws SQLException {
        String code = SecurityUtil.generateOTP();
        OTPToken token = new OTPToken();
        token.setUserId(user.getUserId());
        token.setOtpCode(code);
        // Expiry in 60 minutes for troubleshooting timezone issues
        token.setExpiryTime(new Timestamp(System.currentTimeMillis() + 60 * 60 * 1000));

        otpDAO.createToken(token);
        LOGGER.info(
                "OTP generated and stored for userId: " + user.getUserId() + ". Sending email to: " + user.getEmail());
        EmailUtil.sendOTP(user.getEmail(), code);
    }

    public boolean verifyOTP(int userId, String code, String ipAddress) throws SQLException {
        LOGGER.info("Verifying OTP for userId: " + userId + " with code: " + code);
        Optional<OTPToken> tokenOpt = otpDAO.getLatestToken(userId);

        if (!tokenOpt.isPresent()) {
            LOGGER.warning("No active OTP token found for userId: " + userId);
            return false;
        }

        OTPToken token = tokenOpt.get();
        LOGGER.info("Latest token found: " + token.getOtpCode() + ", Expiry: " + token.getExpiryTime() + ", Used: "
                + token.isUsed());

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (token.getExpiryTime().before(now)) {
            LOGGER.warning(
                    "OTP token expired for userId: " + userId + ". Expiry: " + token.getExpiryTime() + ", Now: " + now);
            return false;
        }

        if (token.isUsed()) {
            LOGGER.warning("OTP token already used for userId: " + userId);
            return false;
        }

        if (token.getAttempts() >= 3) {
            LOGGER.warning("Too many attempts for OTP token in userId: " + userId);
            return false;
        }

        if (token.getOtpCode().equals(code)) {
            otpDAO.markAsUsed(token.getOtpId());
            auditLogDAO.createLog(userId, "LOGIN_SUCCESS", "2FA verification successful", ipAddress);
            LOGGER.info("OTP verification successful for userId: " + userId);
            return true;
        } else {
            otpDAO.incrementAttempts(token.getOtpId());
            auditLogDAO.createLog(userId, "2FA_FAILED", "Invalid OTP attempt", ipAddress);
            LOGGER.warning("Invalid OTP attempt for userId: " + userId + ". Expected: " + token.getOtpCode() + ", Got: "
                    + code);
            return false;
        }
    }
}
