package com.peterrose.peterrose.service;

import com.peterrose.peterrose.dto.request.LoginRequestDTO;
import com.peterrose.peterrose.dto.response.LoginResponseDTO;

/**
 * Authentication service interface
 */
public interface AuthService {

    /**
     * Authenticate user and generate JWT token
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * Validate JWT token
     */
    boolean validateToken(String token);

    /**
     * Get user email from token
     */
    String getEmailFromToken(String token);
}