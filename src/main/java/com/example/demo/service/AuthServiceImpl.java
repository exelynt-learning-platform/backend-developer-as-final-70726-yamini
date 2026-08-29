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

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        // Assign default role to newly registered user
        user.setRole("USER");

        userRepository.save(user);

        RegisterResponse response = new RegisterResponse();

        response.setMessage("User registered successfully");

        response.setEmail(user.getEmail());

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            throw new RuntimeException("Invalid email or password");
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