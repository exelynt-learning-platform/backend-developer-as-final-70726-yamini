package com.example.demo.mapper;

import com.example.demo.dto.ReservationDto;
import com.example.demo.dto.ResourceDto;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.entity.Resource;
import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MapperTest {

    @Test
    void reservationMapper_ToDto_NullReturnsNull() {
        assertThat(ReservationMapper.toDto(null)).isNull();
    }

    @Test
    void reservationMapper_ToDto_MapsAllFields() {
        Reservation r = new Reservation();
        r.setId(10L);
        Resource res = new Resource();
        res.setId(5L);
        r.setResource(res);
        User u = new User();
        u.setId(2L);
        r.setUser(u);
        r.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        r.setEndTime(LocalDateTime.of(2026, 1, 1, 12, 0));
        r.setPrice(BigDecimal.valueOf(150.00));
        r.setStatus(ReservationStatus.CONFIRMED);

        ReservationDto dto = ReservationMapper.toDto(r);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getResourceId()).isEqualTo(5L);
        assertThat(dto.getUserId()).isEqualTo(2L);
        assertThat(dto.getStartTime()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(dto.getEndTime()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(150.00));
        assertThat(dto.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void reservationMapper_ToDto_NullResourceAndUser() {
        Reservation r = new Reservation();
        r.setId(10L);
        r.setResource(null);
        r.setUser(null);

        ReservationDto dto = ReservationMapper.toDto(r);

        assertThat(dto).isNotNull();
        assertThat(dto.getResourceId()).isNull();
        assertThat(dto.getUserId()).isNull();
    }

    @Test
    void resourceMapper_ToDto_NullReturnsNull() {
        assertThat(ResourceMapper.toDto(null)).isNull();
    }

    @Test
    void resourceMapper_ToDto_MapsFields() {
        Resource res = new Resource();
        res.setId(1L);
        res.setName("Room");
        res.setDescription("Desc");
        res.setPrice(BigDecimal.valueOf(50.0));

        ResourceDto dto = ResourceMapper.toDto(res);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Room");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(50.0));
    }

    @Test
    void resourceMapper_ToEntity_NullReturnsNull() {
        assertThat(ResourceMapper.toEntity(null)).isNull();
    }

    @Test
    void resourceMapper_ToEntity_MapsFields() {
        ResourceDto dto = new ResourceDto();
        dto.setId(1L);
        dto.setName("Room");
        dto.setDescription("Desc");
        dto.setPrice(BigDecimal.valueOf(50.0));

        Resource res = ResourceMapper.toEntity(dto);

        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(1L);
        assertThat(res.getName()).isEqualTo("Room");
        assertThat(res.getDescription()).isEqualTo("Desc");
        assertThat(res.getPrice()).isEqualTo(BigDecimal.valueOf(50.0));
    }
}
