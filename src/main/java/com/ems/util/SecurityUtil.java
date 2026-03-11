package com.ems.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    // Password Hashing
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }

    // OTP Generation
    public static String generateOTP() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }

    // CSRF Token Generation
    public static String generateCSRFToken() {
        byte[] token = new byte[32];
        RANDOM.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }

    // Input Validation (Basic)
    public static boolean isValidPassword(String password) {
        // At least 8 chars, one uppercase, one lowercase, one digit, one special char
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password != null && password.matches(pattern);
    }
}

