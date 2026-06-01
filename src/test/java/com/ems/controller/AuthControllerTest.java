package com.ems.controller;

import com.ems.dto.LoginRequest;
import com.ems.dto.OtpVerificationRequest;
import com.ems.model.User;
import com.ems.repository.UserRepository;
import com.ems.security.JwtAuthenticationFilter;
import com.ems.security.JwtUtils;
import com.ems.security.UserDetailsServiceImpl;
import com.ems.service.AuthService;
import com.ems.util.TestLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestLogger.class)
class AuthControllerTest {

    @Autowired private MockMvc       mockMvc;
    @Autowired private ObjectMapper  objectMapper;

    @MockBean private JwtUtils                jwtUtils;
    @MockBean private UserDetailsServiceImpl  userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private AuthService             authService;
    @MockBean private AuthenticationManager   authenticationManager;
    @MockBean private UserRepository          userRepository;

    private static final String EMAIL = "tehilawavumbuzi@gmail.com";

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setUserId(1);
        activeUser.setEmail(EMAIL);
        activeUser.setPasswordHash("$2a$hashed");
        activeUser.setLocked(false);
    }

    // ── POST /api/auth/login ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login → 200 requiresOtp=true for " + EMAIL)
    void login_validCredentials_returns200WithOtpFlag() throws Exception {
        when(authService.authenticate(eq(EMAIL), eq("secret"), anyString()))
                .thenReturn(Optional.of(activeUser));
        doNothing().when(authService).generateAndSendOTP(any(User.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, "secret"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresOtp").value(true))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("POST /api/auth/login → 401 on invalid credentials")
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.authenticate(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login → 403 when account is locked")
    void login_lockedAccount_returns403() throws Exception {
        activeUser.setLocked(true);
        when(authService.authenticate(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(activeUser));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, "secret"))))
                .andExpect(status().isForbidden());
    }

    // ── POST /api/auth/verify-otp ──────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/verify-otp → 200 with JWT for " + EMAIL)
    void verifyOtp_valid_returns200WithToken() throws Exception {
        when(authService.verifyOTP(eq(1), eq("123456"), anyString())).thenReturn(true);
        when(authService.getUserById(1)).thenReturn(activeUser);

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        EMAIL, "$2a$hashed", new ArrayList<>());
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(springUser);
        when(jwtUtils.generateToken(any())).thenReturn("mock.jwt.token");

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OtpVerificationRequest(1, "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("POST /api/auth/verify-otp → 401 on invalid OTP")
    void verifyOtp_invalid_returns401() throws Exception {
        when(authService.verifyOTP(eq(1), eq("000000"), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OtpVerificationRequest(1, "000000"))))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/auth/register ────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/register → 200 for new email " + EMAIL)
    void register_newEmail_returns200() throws Exception {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(authService.register(any(User.class))).thenReturn(activeUser);

        User newUser = new User();
        newUser.setEmail(EMAIL);
        newUser.setPasswordHash("password123");
        newUser.setRoleId(1);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/register → 400 when " + EMAIL + " already exists")
    void register_duplicateEmail_returns400() throws Exception {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        User existing = new User();
        existing.setEmail(EMAIL);
        existing.setPasswordHash("password123");
        existing.setRoleId(1);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existing)))
                .andExpect(status().isBadRequest());
    }
}
