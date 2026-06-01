package com.ems.service;

import com.ems.model.OTPToken;
import com.ems.model.User;
import com.ems.repository.AuditLogRepository;
import com.ems.repository.OTPTokenRepository;
import com.ems.repository.UserRepository;
import com.ems.util.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, TestLogger.class})
class AuthServiceTest {

    @Mock private UserRepository     userRepository;
    @Mock private OTPTokenRepository otpTokenRepository;
    @Mock private AuditLogRepository auditLogRepository;
    @Mock private PasswordEncoder    passwordEncoder;
    @Mock private JavaMailSender     mailSender;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

    private static final String EMAIL = "tehilawavumbuzi@gmail.com";

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setUserId(1);
        activeUser.setEmail(EMAIL);
        activeUser.setPasswordHash("$2a$hashed");
        activeUser.setLocked(false);
        activeUser.setFailedLoginAttempts(0);
    }

    // ── authenticate ───────────────────────────────────────────────────────

    @Test
    @DisplayName("authenticate: valid credentials → returns user")
    void authenticate_validCredentials_returnsUser() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("secret", "$2a$hashed")).thenReturn(true);
        when(userRepository.save(any())).thenReturn(activeUser);
        when(auditLogRepository.save(any())).thenReturn(null);

        Optional<User> result = authService.authenticate(EMAIL, "secret", "127.0.0.1");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
        assertThat(result.get().getFailedLoginAttempts()).isEqualTo(0);
    }

    @Test
    @DisplayName("authenticate: unknown email → returns empty")
    void authenticate_unknownEmail_returnsEmpty() {
        when(userRepository.findByEmail("unknown@x.com")).thenReturn(Optional.empty());
        when(auditLogRepository.save(any())).thenReturn(null);

        Optional<User> result = authService.authenticate("unknown@x.com", "pass", "127.0.0.1");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("authenticate: wrong password → increments failed attempts")
    void authenticate_wrongPassword_incrementsAttempts() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrong", "$2a$hashed")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(activeUser);
        when(auditLogRepository.save(any())).thenReturn(null);

        Optional<User> result = authService.authenticate(EMAIL, "wrong", "127.0.0.1");

        assertThat(result).isEmpty();
        assertThat(activeUser.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("authenticate: 5 failures → account locked")
    void authenticate_fiveFailures_locksAccount() {
        activeUser.setFailedLoginAttempts(4);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(activeUser);
        when(auditLogRepository.save(any())).thenReturn(null);

        authService.authenticate(EMAIL, "wrong", "127.0.0.1");

        assertThat(activeUser.isLocked()).isTrue();
    }

    @Test
    @DisplayName("authenticate: locked account → skips password check")
    void authenticate_lockedAccount_skipsPasswordCheck() {
        activeUser.setLocked(true);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser));
        when(auditLogRepository.save(any())).thenReturn(null);

        Optional<User> result = authService.authenticate(EMAIL, "any", "127.0.0.1");

        assertThat(result).isPresent();
        assertThat(result.get().isLocked()).isTrue();
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ── generateAndSendOTP ─────────────────────────────────────────────────

    @Test
    @DisplayName("generateAndSendOTP: saves token and sends email to " + EMAIL)
    void generateAndSendOTP_savesTokenAndSendsEmail() {
        when(otpTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        authService.generateAndSendOTP(activeUser);

        verify(otpTokenRepository).save(any(OTPToken.class));
        ArgumentCaptor<SimpleMailMessage> cap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(cap.capture());
        assertThat(cap.getValue().getTo()).contains(EMAIL);
        assertThat(cap.getValue().getText()).contains("OTP code");
    }

    // ── verifyOTP ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("verifyOTP: correct code → returns true, marks token used")
    void verifyOTP_validCode_returnsTrue() {
        OTPToken token = buildToken("123456", 0, false, 300_000);
        when(otpTokenRepository.findTopByUserIdAndIsUsedFalseOrderByExpiryTimeDesc(1))
                .thenReturn(Optional.of(token));
        when(otpTokenRepository.save(any())).thenReturn(token);
        when(auditLogRepository.save(any())).thenReturn(null);

        assertThat(authService.verifyOTP(1, "123456", "127.0.0.1")).isTrue();
        assertThat(token.isUsed()).isTrue();
    }

    @Test
    @DisplayName("verifyOTP: wrong code → returns false, increments attempts")
    void verifyOTP_wrongCode_returnsFalse() {
        OTPToken token = buildToken("123456", 0, false, 300_000);
        when(otpTokenRepository.findTopByUserIdAndIsUsedFalseOrderByExpiryTimeDesc(1))
                .thenReturn(Optional.of(token));
        when(otpTokenRepository.save(any())).thenReturn(token);
        when(auditLogRepository.save(any())).thenReturn(null);

        assertThat(authService.verifyOTP(1, "999999", "127.0.0.1")).isFalse();
        assertThat(token.getAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("verifyOTP: expired token → returns false")
    void verifyOTP_expiredToken_returnsFalse() {
        OTPToken token = buildToken("123456", 0, false, -1000); // expired
        when(otpTokenRepository.findTopByUserIdAndIsUsedFalseOrderByExpiryTimeDesc(1))
                .thenReturn(Optional.of(token));

        assertThat(authService.verifyOTP(1, "123456", "127.0.0.1")).isFalse();
    }

    @Test
    @DisplayName("verifyOTP: no token found → returns false")
    void verifyOTP_noToken_returnsFalse() {
        when(otpTokenRepository.findTopByUserIdAndIsUsedFalseOrderByExpiryTimeDesc(99))
                .thenReturn(Optional.empty());

        assertThat(authService.verifyOTP(99, "123456", "127.0.0.1")).isFalse();
    }

    @Test
    @DisplayName("verifyOTP: max attempts reached → returns false")
    void verifyOTP_maxAttempts_returnsFalse() {
        OTPToken token = buildToken("123456", 3, false, 300_000);
        when(otpTokenRepository.findTopByUserIdAndIsUsedFalseOrderByExpiryTimeDesc(1))
                .thenReturn(Optional.of(token));

        assertThat(authService.verifyOTP(1, "123456", "127.0.0.1")).isFalse();
    }

    // ── register ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: encodes password, sets locked=false, saves user with " + EMAIL)
    void register_encodesPasswordAndSaves() {
        User newUser = new User();
        newUser.setEmail(EMAIL);
        newUser.setPasswordHash("plaintext");

        when(passwordEncoder.encode("plaintext")).thenReturn("$2a$encoded");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setUserId(10);
            return u;
        });

        User saved = authService.register(newUser);

        assertThat(saved.getPasswordHash()).isEqualTo("$2a$encoded");
        assertThat(saved.isLocked()).isFalse();
        assertThat(saved.getFailedLoginAttempts()).isEqualTo(0);
        verify(userRepository).save(any(User.class));
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private OTPToken buildToken(String code, int attempts, boolean used, long expiryOffsetMs) {
        OTPToken t = new OTPToken();
        t.setOtpCode(code);
        t.setAttempts(attempts);
        t.setUsed(used);
        t.setExpiryTime(new Timestamp(System.currentTimeMillis() + expiryOffsetMs));
        return t;
    }
}
