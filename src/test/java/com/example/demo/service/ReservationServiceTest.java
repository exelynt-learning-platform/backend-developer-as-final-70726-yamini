package com.example.demo.service;

import com.example.demo.dto.ReservationDto;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.entity.Resource;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ReservationNotFoundException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.ResourceRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User testUser;
    private Resource testResource;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("user@example.com");
        testUser.setRole("USER");

        testResource = new Resource();
        testResource.setId(10L);
        testResource.setName("Conference Room A");
        testResource.setDescription("Large room");
        testResource.setPrice(BigDecimal.valueOf(100.00));

        testReservation = new Reservation();
        testReservation.setId(100L);
        testReservation.setUser(testUser);
        testReservation.setResource(testResource);
        testReservation.setStartTime(LocalDateTime.now().plusHours(1));
        testReservation.setEndTime(LocalDateTime.now().plusHours(2));
        testReservation.setPrice(BigDecimal.valueOf(100.00));
        testReservation.setStatus(ReservationStatus.PENDING);
    }

    @Test
    void createReservation_Success() {
        ReservationDto dto = new ReservationDto();
        dto.setResourceId(10L);
        dto.setStartTime(LocalDateTime.now().plusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(3));

        when(resourceRepository.findById(10L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(100L);
            return r;
        });

        ReservationDto result = reservationService.createReservation(dto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getResourceId()).isEqualTo(10L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(100.00));
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void createReservation_NullStartOrEndTime_ThrowsBadRequest() {
        ReservationDto dto = new ReservationDto();
        dto.setResourceId(10L);
        dto.setStartTime(null);
        dto.setEndTime(LocalDateTime.now().plusHours(1));

        assertThatThrownBy(() -> reservationService.createReservation(dto, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("startTime and endTime are required");
    }

    @Test
    void createReservation_EndTimeBeforeStartTime_ThrowsBadRequest() {
        ReservationDto dto = new ReservationDto();
        dto.setResourceId(10L);
        dto.setStartTime(LocalDateTime.now().plusHours(2));
        dto.setEndTime(LocalDateTime.now().plusHours(1));

        assertThatThrownBy(() -> reservationService.createReservation(dto, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("endTime must be after startTime");
    }

    @Test
    void createReservation_ResourceNotFound_ThrowsResourceNotFound() {
        ReservationDto dto = new ReservationDto();
        dto.setResourceId(999L);
        dto.setStartTime(LocalDateTime.now().plusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(2));

        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(dto, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resource not found");
    }

    @Test
    void createReservation_UserNotFound_ThrowsUnauthorized() {
        ReservationDto dto = new ReservationDto();
        dto.setResourceId(10L);
        dto.setStartTime(LocalDateTime.now().plusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(2));

        when(resourceRepository.findById(10L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(dto, 999L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void createReservation_InvalidResourcePrice_ThrowsBadRequest() {
        testResource.setPrice(null);
        ReservationDto dto = new ReservationDto();
        dto.setResourceId(10L);
        dto.setStartTime(LocalDateTime.now().plusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(2));

        when(resourceRepository.findById(10L)).thenReturn(Optional.of(testResource));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> reservationService.createReservation(dto, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Resource has invalid price");
    }

    @Test
    void getReservation_AsOwner_Success() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(testReservation));

        ReservationDto result = reservationService.getReservation(100L, 1L, false);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    void getReservation_AsAdmin_Success() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(testReservation));

        ReservationDto result = reservationService.getReservation(100L, 999L, true);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    void getReservation_NotFound_ThrowsReservationNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getReservation(999L, 1L, false))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("Reservation not found");
    }

    @Test
    void getReservation_NotOwnerAndNotAdmin_ThrowsUnauthorized() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(testReservation));

        assertThatThrownBy(() -> reservationService.getReservation(100L, 2L, false))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void searchReservationsForAdmin_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> page = new PageImpl<>(List.of(testReservation));
        when(reservationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<ReservationDto> result = reservationService.searchReservationsForAdmin(ReservationStatus.PENDING, BigDecimal.valueOf(50), BigDecimal.valueOf(150), pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchReservationsForUser_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> page = new PageImpl<>(List.of(testReservation));
        when(reservationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<ReservationDto> result = reservationService.searchReservationsForUser(1L, ReservationStatus.PENDING, BigDecimal.valueOf(50), null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchReservations_InvalidPriceRange_ThrowsBadRequest() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThatThrownBy(() -> reservationService.searchReservationsForAdmin(null, BigDecimal.valueOf(200), BigDecimal.valueOf(100), pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("minPrice cannot be greater than maxPrice");
    }

    @Test
    void updateStatus_Success() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationDto result = reservationService.updateStatus(100L, ReservationStatus.CONFIRMED);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void updateStatus_NotFound_ThrowsReservationNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.updateStatus(999L, ReservationStatus.CONFIRMED))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    @Test
    void deleteReservation_ById_Success() {
        when(reservationRepository.existsById(100L)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(100L);

        reservationService.deleteReservation(100L);

        verify(reservationRepository, times(1)).deleteById(100L);
    }

    @Test
    void deleteReservation_ById_NotFound() {
        when(reservationRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> reservationService.deleteReservation(999L))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    @Test
    void deleteReservation_AsAdmin_Success() {
        when(reservationRepository.existsById(100L)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(100L);

        reservationService.deleteReservation(100L, 999L, true);

        verify(reservationRepository, times(1)).deleteById(100L);
    }

    @Test
    void deleteReservation_AsUser_Success() {
        when(reservationRepository.deleteByIdAndUserId(100L, 1L)).thenReturn(1);

        reservationService.deleteReservation(100L, 1L, false);

        verify(reservationRepository, times(1)).deleteByIdAndUserId(100L, 1L);
    }

    @Test
    void deleteReservation_AsUser_NotFound() {
        when(reservationRepository.deleteByIdAndUserId(999L, 1L)).thenReturn(0);

        assertThatThrownBy(() -> reservationService.deleteReservation(999L, 1L, false))
                .isInstanceOf(ReservationNotFoundException.class);
    }
}

