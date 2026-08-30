package com.example.demo.dto;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.RegisterResponse;
import com.example.demo.dto.reservation.ReservationRequest;
import com.example.demo.dto.reservation.ReservationResponse;
import com.example.demo.dto.resource.ResourceRequest;
import com.example.demo.dto.resource.ResourceResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ApiError;
import com.example.demo.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoAndEntityTest {

    @Test
    void testReservationDto() {
        ReservationDto dto = new ReservationDto();
        dto.setId(1L);
        dto.setResourceId(2L);
        dto.setUserId(3L);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setPrice(BigDecimal.valueOf(100));
        dto.setStatus(ReservationStatus.PENDING);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getResourceId()).isEqualTo(2L);
        assertThat(dto.getUserId()).isEqualTo(3L);
        assertThat(dto.getStartTime()).isEqualTo(start);
        assertThat(dto.getEndTime()).isEqualTo(end);
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(dto.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void testResourceDto() {
        ResourceDto dto = new ResourceDto();
        dto.setId(1L);
        dto.setName("Room");
        dto.setDescription("Desc");
        dto.setPrice(BigDecimal.valueOf(50));

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Room");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    void testAuthDtos() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("user@example.com");
        loginReq.setPassword("pass");
        assertThat(loginReq.getEmail()).isEqualTo("user@example.com");
        assertThat(loginReq.getPassword()).isEqualTo("pass");
        assertThat(loginReq.toString()).contains("user@example.com");

        LoginResponse loginResp = new LoginResponse();
        loginResp.setMessage("OK");
        loginResp.setEmail("user@example.com");
        loginResp.setToken("tok");
        loginResp.setTokenType("Bearer");
        assertThat(loginResp.getMessage()).isEqualTo("OK");
        assertThat(loginResp.getEmail()).isEqualTo("user@example.com");
        assertThat(loginResp.getToken()).isEqualTo("tok");
        assertThat(loginResp.getTokenType()).isEqualTo("Bearer");
        assertThat(loginResp.toString()).contains("user@example.com");

        RegisterRequest regReq = new RegisterRequest();
        regReq.setName("Name");
        regReq.setEmail("email@test.com");
        regReq.setPassword("pwd123");
        assertThat(regReq.getName()).isEqualTo("Name");
        assertThat(regReq.getEmail()).isEqualTo("email@test.com");
        assertThat(regReq.getPassword()).isEqualTo("pwd123");

        RegisterResponse regResp = new RegisterResponse();
        regResp.setMessage("Created");
        regResp.setEmail("email@test.com");
        assertThat(regResp.getMessage()).isEqualTo("Created");
        assertThat(regResp.getEmail()).isEqualTo("email@test.com");
    }

    @Test
    void testReservationRequestAndResponse() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        ReservationRequest req = new ReservationRequest(1L, start, end);
        assertThat(req.getResourceId()).isEqualTo(1L);
        assertThat(req.getStartTime()).isEqualTo(start);
        assertThat(req.getEndTime()).isEqualTo(end);

        req.setResourceId(2L);
        req.setStartTime(start.plusHours(1));
        req.setEndTime(end.plusHours(1));
        assertThat(req.getResourceId()).isEqualTo(2L);

        ReservationResponse resp = new ReservationResponse(10L, 2L, 3L, start, end, BigDecimal.TEN, ReservationStatus.CONFIRMED);
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getResourceId()).isEqualTo(2L);
        assertThat(resp.getUserId()).isEqualTo(3L);
        assertThat(resp.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(resp.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

        resp.setId(11L);
        resp.setResourceId(3L);
        resp.setUserId(4L);
        resp.setStartTime(start);
        resp.setEndTime(end);
        resp.setPrice(BigDecimal.ONE);
        resp.setStatus(ReservationStatus.CANCELLED);
        assertThat(resp.getId()).isEqualTo(11L);
    }

    @Test
    void testResourceRequestAndResponse() {
        ResourceRequest req = new ResourceRequest("Mic", "Wireless", BigDecimal.valueOf(15));
        assertThat(req.getName()).isEqualTo("Mic");
        assertThat(req.getDescription()).isEqualTo("Wireless");
        assertThat(req.getPrice()).isEqualTo(BigDecimal.valueOf(15));

        req.setName("Mic 2");
        req.setDescription("Corded");
        req.setPrice(BigDecimal.valueOf(10));
        assertThat(req.getName()).isEqualTo("Mic 2");

        ResourceResponse resp = new ResourceResponse(1L, "Mic", "Wireless", BigDecimal.valueOf(15));
        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getName()).isEqualTo("Mic");

        resp.setId(2L);
        resp.setName("Mic 2");
        resp.setDescription("Corded");
        resp.setPrice(BigDecimal.valueOf(10));
        assertThat(resp.getId()).isEqualTo(2L);
    }

    @Test
    void testEntities() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("j@test.com");
        user.setPassword("hashed");
        user.setRole("USER");
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getEmail()).isEqualTo("j@test.com");
        assertThat(user.getPassword()).isEqualTo("hashed");
        assertThat(user.getRole()).isEqualTo("USER");

        Role role = new Role(RoleName.ROLE_ADMIN);
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo(RoleName.ROLE_USER);

        Reservation reservation = new Reservation();
        reservation.setId(5L);
        reservation.setStatus(ReservationStatus.PENDING);
        assertThat(reservation.getId()).isEqualTo(5L);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);

        Reservation other = new Reservation();
        other.setId(5L);
        assertThat(reservation.equals(other)).isTrue();
        assertThat(reservation.hashCode()).isEqualTo(other.hashCode());
        assertThat(reservation.equals(new Object())).isFalse();

        ApiError error = new ApiError(404, "Not Found", "Detail", "/path");
        assertThat(error.getStatus()).isEqualTo(404);
        assertThat(error.getError()).isEqualTo("Not Found");
        assertThat(error.getMessage()).isEqualTo("Detail");
        assertThat(error.getPath()).isEqualTo("/path");
        assertThat(error.getTimestamp()).isNotNull();

        error.setStatus(500);
        error.setError("Error");
        error.setMessage("Msg");
        error.setPath("/other");
        assertThat(error.getStatus()).isEqualTo(500);
    }

    @Test
    void testUserNotFoundException() {
        UserNotFoundException ex1 = new UserNotFoundException("User not found");
        assertThat(ex1.getMessage()).isEqualTo("User not found");

        Throwable cause = new RuntimeException("root cause");
        UserNotFoundException ex2 = new UserNotFoundException("User not found", cause);
        assertThat(ex2.getCause()).isEqualTo(cause);
    }
}
