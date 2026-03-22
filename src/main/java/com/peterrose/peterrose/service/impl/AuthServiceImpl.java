package com.peterrose.peterrose.service.impl;

import com.peterrose.peterrose.dto.request.LoginRequestDTO;
import com.peterrose.peterrose.dto.response.LoginResponseDTO;
import com.peterrose.peterrose.model.User;
import com.peterrose.peterrose.repository.UserRepository;
import com.peterrose.peterrose.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Authentication service implementation
 * All authentication business logic here
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", loginRequest.getEmail());
                    return new RuntimeException("Invalid email or password");
                });

        // Check if user is active
        if (!user.getActive()) {
            log.warn("Login failed: User is inactive - {}", loginRequest.getEmail());
            throw new RuntimeException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password - {}", loginRequest.getEmail());
            throw new RuntimeException("Invalid email or password");
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail(), user.getRole().getValue());

        log.info("✅ Login successful for user: {}", user.getEmail());

        // Return response
        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().getValue())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String email = jwtService.extractEmail(token);
            return userRepository.findByEmail(email).isPresent()
                    && jwtService.validateToken(token, email);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        return jwtService.extractEmail(token);
    }
}