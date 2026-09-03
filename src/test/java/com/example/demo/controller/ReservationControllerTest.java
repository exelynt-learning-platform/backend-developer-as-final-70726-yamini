package com.example.demo.controller;

import com.example.demo.dto.ReservationDto;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ReservationNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@WebMvcTest(ReservationController.class)
@EnableMethodSecurity
@Import({GlobalExceptionHandler.class, com.example.demo.config.SecurityConfig.class})
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private UserContextService userContextService;

    @MockBean
    private com.example.demo.security.JwtService jwtService;

    @MockBean
    private com.example.demo.security.CustomUserDetailsService customUserDetailsService;

    private User testUser;
    private User testAdmin;
    private ReservationDto sampleDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setName("User");
        testUser.setRole("USER");

        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setEmail("admin@example.com");
        testAdmin.setName("Admin");
        testAdmin.setRole("ADMIN");

        sampleDto = new ReservationDto();
        sampleDto.setId(100L);
        sampleDto.setResourceId(10L);
        sampleDto.setUserId(1L);
        sampleDto.setStartTime(LocalDateTime.of(2026, 9, 1, 10, 0));
        sampleDto.setEndTime(LocalDateTime.of(2026, 9, 1, 12, 0));
        sampleDto.setPrice(BigDecimal.valueOf(100.00));
        sampleDto.setStatus(ReservationStatus.PENDING);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void createReservation_Success() throws Exception {
        when(userContextService.currentUserId(any())).thenReturn(1L);
        when(userContextService.isAdmin(any())).thenReturn(false);
        when(reservationService.createReservation(any(ReservationDto.class), eq(1L))).thenReturn(sampleDto);

        mockMvc.perform(post("/api/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.resourceId").value(10L));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void getReservation_Success() throws Exception {
        when(userContextService.currentUserId(any())).thenReturn(1L);
        when(userContextService.isAdmin(any())).thenReturn(false);
        when(reservationService.getReservation(100L, 1L, false)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/reservations/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void searchReservations_Admin_Success() throws Exception {
        Page<ReservationDto> page = new PageImpl<>(List.of(sampleDto));
        when(reservationService.searchReservationsForAdmin(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/reservations")
                        .param("status", "PENDING")
                        .param("minPrice", "50")
                        .param("maxPrice", "200")
                        .param("sort", "price,desc")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(100L));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void searchReservations_User_Success() throws Exception {
        when(userContextService.currentUserId(any())).thenReturn(1L);
        when(userContextService.isAdmin(any())).thenReturn(false);
        Page<ReservationDto> page = new PageImpl<>(List.of(sampleDto));
        when(reservationService.searchReservationsForUser(eq(1L), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/reservations")
                        .param("sort", "id")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(100L));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void searchReservations_InvalidStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid status: INVALID_STATUS"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void searchReservations_InvalidSortDirection_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .param("sort", "id,sideways"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid sort direction: sideways; expected 'asc' or 'desc'"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void searchReservations_InvalidPage_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'page' must be >= 0"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void searchReservations_InvalidSize_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'size' must be > 0 and <= 200"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateStatus_Admin_Success() throws Exception {
        sampleDto.setStatus(ReservationStatus.CONFIRMED);
        when(reservationService.updateStatus(100L, ReservationStatus.CONFIRMED)).thenReturn(sampleDto);

        mockMvc.perform(put("/api/reservations/100/status")
                        .with(csrf())
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updateStatus_InvalidStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/reservations/100/status")
                        .with(csrf())
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid status: INVALID_STATUS"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void updateStatus_User_Forbidden() throws Exception {
        mockMvc.perform(put("/api/reservations/100/status")
                        .with(csrf())
                        .param("status", "CONFIRMED"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deleteReservation_Admin_Success() throws Exception {
        doNothing().when(reservationService).deleteReservation(100L);

        mockMvc.perform(delete("/api/reservations/100")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteReservation(100L);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void deleteReservation_User_Success() throws Exception {
        when(userContextService.currentUserId(any())).thenReturn(1L);
        when(userContextService.isAdmin(any())).thenReturn(false);
        doNothing().when(reservationService).deleteReservation(100L, 1L, false);

        mockMvc.perform(delete("/api/reservations/100")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteReservation(100L, 1L, false);
    }
}

