package com.example.demo.controller;

import com.example.demo.dto.ResourceDto;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@WebMvcTest(ResourceController.class)
@EnableMethodSecurity
@Import({GlobalExceptionHandler.class, com.example.demo.config.SecurityConfig.class})
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private com.example.demo.security.JwtService jwtService;

    @MockBean
    private com.example.demo.security.CustomUserDetailsService customUserDetailsService;

    private ResourceDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = new ResourceDto();
        sampleDto.setId(1L);
        sampleDto.setName("Auditorium");
        sampleDto.setDescription("Large event space");
        sampleDto.setPrice(BigDecimal.valueOf(200.00));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void list_Success() throws Exception {
        when(resourceService.findAll()).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Auditorium"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void get_Found_Success() throws Exception {
        when(resourceService.findById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Auditorium"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void get_NotFound_ReturnsNotFound() throws Exception {
        when(resourceService.findById(999L)).thenThrow(new ResourceNotFoundException("Resource not found: 999"));

        mockMvc.perform(get("/api/resources/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found: 999"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void create_AsAdmin_Success() throws Exception {
        when(resourceService.create(any(ResourceDto.class))).thenReturn(sampleDto);

        mockMvc.perform(post("/api/resources")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Auditorium"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void create_AsUser_Forbidden() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void update_AsAdmin_Success() throws Exception {
        when(resourceService.update(eq(1L), any(ResourceDto.class))).thenReturn(sampleDto);

        mockMvc.perform(put("/api/resources/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void update_AsUser_Forbidden() throws Exception {
        mockMvc.perform(put("/api/resources/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void delete_AsAdmin_Success() throws Exception {
        doNothing().when(resourceService).delete(1L);

        mockMvc.perform(delete("/api/resources/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(resourceService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void delete_AsUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/resources/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}

