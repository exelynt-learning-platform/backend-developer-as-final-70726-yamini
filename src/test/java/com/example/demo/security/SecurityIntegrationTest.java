package com.example.demo.security;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String getAdminToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@example.com");
        req.setPassword("adminpass");

        String res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<?, ?> map = objectMapper.readValue(res, Map.class);
        return (String) map.get("token");
    }

    private String getUserToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("userpass");

        String res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<?, ?> map = objectMapper.readValue(res, Map.class);
        return (String) map.get("token");
    }

    @Test
    void publicEndpoints_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void securedEndpoints_WithoutToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isForbidden());
    }

    @Test
    void securedEndpoints_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/resources")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securedEndpoints_WithValidToken_ReturnsSuccess() throws Exception {
        String token = getUserToken();
        mockMvc.perform(get("/api/resources")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoints_WithUserToken_ReturnsForbidden() throws Exception {
        String token = getUserToken();
        mockMvc.perform(get("/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void jwtService_GeneratesAndValidatesToken() {
        String email = "admin@example.com";
        String token = jwtService.generateToken(email);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(email);
        assertThat(jwtService.isTokenValid(token, email)).isTrue();
        assertThat(jwtService.isTokenValid(token, "other@example.com")).isFalse();
        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    @Test
    void customUserDetails_MethodsWorkProperly() {
        User user = userRepository.findByEmail("admin@example.com").orElseThrow();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");
        assertThat(userDetails.getPassword()).isNotBlank();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getUser()).isEqualTo(user);
    }
}

