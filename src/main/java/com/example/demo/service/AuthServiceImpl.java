package com.example.demo.service;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.RegisterResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final Logger logger =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =====================================================
    // REGISTER
    // =====================================================

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.example.demo.exception.BadRequestException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Encrypt password using BCrypt
        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        // Default role
        user.setRole("USER");

        // Save user
        User saved = userRepository.saveAndFlush(user);

        RegisterResponse response = new RegisterResponse();

        response.setMessage("User registered successfully");
        response.setEmail(saved.getEmail());

        return response;
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @Override
    public LoginResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {

            logger.warn(
                    "Authentication failed: unknown email or user not found"
            );

            throw new com.example.demo.exception.AuthenticationException(
                    "Invalid email or password"
            );
        }

        // Verify password
        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            logger.warn(
                    "Authentication failed: bad credentials provided"
            );

            throw new com.example.demo.exception.AuthenticationException(
                    "Invalid email or password"
            );
        }

        // =================================================
        // LOGIN SUCCESS
        // =================================================

        LoginResponse response = new LoginResponse();

        response.setMessage("Login successful");
        response.setEmail(user.getEmail());

        // Return role to frontend
        response.setRole("ROLE_" + user.getRole());

        // Generate JWT
        String token =
                jwtService.generateToken(user.getEmail());

        response.setToken(token);

        return response;
    }
}