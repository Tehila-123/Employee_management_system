package com.ems.controller;

import com.ems.dto.*;
import com.ems.model.User;
import com.ems.security.JwtUtils;
import com.ems.security.UserDetailsServiceImpl;
import com.ems.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user login, registration, and OTP verification")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    @Operation(summary = "Login and request OTP", description = "Authenticates user credentials and sends an OTP to their email if successful.")
    @ApiResponse(responseCode = "200", description = "OTP sent to email")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "403", description = "Account locked")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        Optional<User> userOpt = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword(), ipAddress);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new AuthResponse(null, "Invalid credentials", false));
        }

        User user = userOpt.get();
        if (user.isLocked()) {
            return ResponseEntity.status(403).body(new AuthResponse(null, "Account locked", false));
        }

        // Generate OTP and send email
        authService.generateAndSendOTP(user);

        return ResponseEntity.ok(new AuthResponse(null, "OTP sent to your email", true, user.getUserId()));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verifies the OTP sent to the user and returns a JWT token if valid.")
    @ApiResponse(responseCode = "200", description = "Login successful, JWT returned")
    @ApiResponse(responseCode = "401", description = "Invalid or expired OTP")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest otpRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        boolean isValid = authService.verifyOTP(otpRequest.getUserId(), otpRequest.getCode(), ipAddress);

        if (!isValid) {
            return ResponseEntity.status(401).body(new AuthResponse(null, "Invalid or expired OTP", true));
        }

        // OTP valid, generate JWT
        User user = authService.getUserById(otpRequest.getUserId()); // Need to add this to AuthService
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String jwt = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt, "Login successful", false));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account.")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Email already exists")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User registeredUser = authService.register(user);
        return ResponseEntity.ok(registeredUser);
    }

    @Autowired
    private com.ems.repository.UserRepository userRepository;
}

