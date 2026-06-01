package com.ems.repository;

import com.ems.model.OTPToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OTPTokenRepository extends JpaRepository<OTPToken, Integer> {
    Optional<OTPToken> findByUserIdAndOtpCodeAndIsUsedFalse(int userId, String otpCode);
    Optional<OTPToken> findTopByUserIdAndIsUsedFalseOrderByExpiryTimeDesc(int userId);
}
