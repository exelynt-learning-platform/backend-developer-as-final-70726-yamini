package com.example.demo.controller;

import com.example.demo.dto.ResourceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String loginAndGetToken(String email, String password) throws Exception {
        Map<String, String> req = Map.of("email", email, "password", password);
        String result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Map<?, ?> resp = objectMapper.readValue(result, Map.class);
        return (String) resp.get("token");
    }

    @Test
    void adminCanCreateResource() throws Exception {
        String token = loginAndGetToken("admin@example.com", "adminpass");

        ResourceDto dto = new ResourceDto();
        dto.setName("Conference Room");
        dto.setDescription("Test room");
        dto.setPrice(java.math.BigDecimal.valueOf(100));

        String created = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResourceDto resp = objectMapper.readValue(created, ResourceDto.class);
        assertThat(resp.getId()).isNotNull();
        assertThat(resp.getName()).isEqualTo("Conference Room");
    }

    @Test
    void anonymousCannotCreateResource() throws Exception {
        ResourceDto dto = new ResourceDto();
        dto.setName("X");
        dto.setPrice(java.math.BigDecimal.ZERO);

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
