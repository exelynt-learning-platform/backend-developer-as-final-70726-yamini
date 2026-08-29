package com.example.demo.service;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.RegisterResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.example.demo.exception.BadRequestException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        // Assign default role to newly registered user
        user.setRole("USER");

        // persist and flush so generated id and other DB-populated fields are available
        User saved = userRepository.saveAndFlush(user);

        RegisterResponse response = new RegisterResponse();
        response.setMessage("User registered successfully");
        response.setEmail(saved.getEmail());

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // Lookup user and validate password. Log failures for monitoring but
        // always throw a generic authentication exception to the client.
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            logger.warn("Authentication failed for unknown email: {}", request.getEmail());
            throw new com.example.demo.exception.AuthenticationException("Invalid email or password");
        }

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            logger.warn("Authentication failed for email: {} (bad credentials)", request.getEmail());
            throw new com.example.demo.exception.AuthenticationException("Invalid email or password");
        }

        LoginResponse response = new LoginResponse();

        response.setMessage("Login successful");

        response.setEmail(user.getEmail());

        // generate JWT token
        String token = jwtService.generateToken(user.getEmail());
        response.setToken(token);

        return response;
    }
}