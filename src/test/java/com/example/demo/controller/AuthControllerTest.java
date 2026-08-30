package com.example.demo.controller;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.RegisterResponse;
import com.example.demo.exception.AuthenticationException;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private com.example.demo.security.JwtService jwtService;

    @MockBean
    private com.example.demo.security.CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    void register_Success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Alice");
        req.setEmail("alice@example.com");
        req.setPassword("password123");

        RegisterResponse resp = new RegisterResponse();
        resp.setEmail("alice@example.com");
        resp.setMessage("User registered successfully");

        when(authService.register(any(RegisterRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    @WithMockUser
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Alice");
        req.setEmail("not-an-email");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_ShortPassword_ReturnsBadRequest() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Alice");
        req.setEmail("alice@example.com");
        req.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_Success() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@example.com");
        req.setPassword("password123");

        LoginResponse resp = new LoginResponse();
        resp.setEmail("alice@example.com");
        resp.setMessage("Login successful");
        resp.setToken("sample-jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.token").value("sample-jwt-token"));
    }

    @Test
    @WithMockUser
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@example.com");
        req.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class))).thenThrow(new AuthenticationException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}

