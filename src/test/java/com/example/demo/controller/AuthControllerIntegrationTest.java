package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.example.demo.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            com.example.demo.entity.User admin = new com.example.demo.entity.User();
            admin.setName("Admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }

    @Test
    void loginReturnsTokenForSeededUser() throws Exception {
        Map<String, String> req = Map.of(
                "email", "admin@example.com",
                "password", "adminpass"
        );

        String result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<?, ?> resp = objectMapper.readValue(result, Map.class);
        assertThat(resp.get("token")).isNotNull();
        assertThat(((String) resp.get("token")).length()).isGreaterThan(20);
    }
}
