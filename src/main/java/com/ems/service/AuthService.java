package com.ems.service;

import com.ems.model.AuditLog;
import com.ems.model.OTPToken;
import com.ems.model.User;
import com.ems.repository.AuditLogRepository;
import com.ems.repository.OTPTokenRepository;
import com.ems.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPTokenRepository otpTokenRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public Optional<User> authenticate(String email, String password, String ipAddress) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            logAction(0, "LOGIN_FAILED", "Invalid email: " + email, ipAddress);
            return Optional.empty();
        }

        User user = userOpt.get();

        if (user.isLocked()) {
            logAction(user.getUserId(), "LOGIN_LOCKED", "Locked account attempt", ipAddress);
            return Optional.of(user);
        }

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            logAction(user.getUserId(), "LOGIN_STEP1_SUCCESS", "Password verification successful", ipAddress);
            return Optional.of(user);
        } else {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setLocked(true);
                logAction(user.getUserId(), "ACCOUNT_LOCKED", "5 failed attempts", ipAddress);
            }
            userRepository.save(user);
            logAction(user.getUserId(), "LOGIN_FAILED", "Invalid password", ipAddress);
            return Optional.empty();
        }
    }

    @Transactional
    public void generateAndSendOTP(User user) {
        String code = String.format("%06d", new Random().nextInt(999999));
        OTPToken token = new OTPToken();
        token.setUserId(user.getUserId());
        token.setOtpCode(code);
        token.setExpiryTime(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 minutes
        token.setAttempts(0);
        token.setUsed(false);

        otpTokenRepository.save(token);
        sendEmail(user.getEmail(), "Your OTP Code", "Your OTP code is: " + code);
    }

    @Transactional
    public boolean verifyOTP(int userId, String code, String ipAddress) {
        Optional<OTPToken> tokenOpt = otpTokenRepository.findTopByUserIdAndIsUsedFalseOrderByExpiryTimeDesc(userId);

        if (tokenOpt.isEmpty()) return false;

        OTPToken token = tokenOpt.get();

        if (token.getExpiryTime().before(new Timestamp(System.currentTimeMillis()))) return false;
        if (token.getAttempts() >= 3) return false;

        if (token.getOtpCode().equals(code)) {
            token.setUsed(true);
            otpTokenRepository.save(token);
            logAction(userId, "LOGIN_SUCCESS", "2FA verification successful", ipAddress);
            return true;
        } else {
            token.setAttempts(token.getAttempts() + 1);
            otpTokenRepository.save(token);
            logAction(userId, "2FA_FAILED", "Invalid OTP attempt", ipAddress);
            return false;
        }
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public User register(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setLocked(false);
        user.setFailedLoginAttempts(0);
        return userRepository.save(user);
    }

    private void logAction(int userId, String action, String description, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

