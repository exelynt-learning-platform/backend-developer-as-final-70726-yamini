package com.example.demo.controller;

import com.example.demo.dto.ReservationDto;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final com.example.demo.service.UserContextService userContextService;

    public ReservationController(ReservationService reservationService, com.example.demo.service.UserContextService userContextService) {
        this.reservationService = reservationService;
        this.userContextService = userContextService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ReservationDto> create(@RequestBody ReservationDto dto, Authentication authentication) {
        Long userId = userContextService.currentUserId(authentication);
        ReservationDto created = reservationService.createReservation(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ReservationDto get(@PathVariable Long id, Authentication authentication) {
        Long requesterId = userContextService.currentUserId(authentication);
        boolean admin = userContextService.isAdmin(authentication);
        return reservationService.getReservation(id, requesterId, admin);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<ReservationDto> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            Authentication authentication
    ) {
        ReservationStatus rs = parseReservationStatus(status);
        PageRequest pageable = com.example.demo.util.QueryValidationUtils.createPageRequest(
                page, size, sort,
                List.of("id", "startTime", "endTime", "price", "status", "resource.id", "user.id"));

        if (userContextService.isAdmin(authentication)) {
            return reservationService.searchReservationsForAdmin(rs, minPrice, maxPrice, pageable);
        }
        if (authentication == null) {
            throw new UnauthorizedException("Unauthenticated");
        }
        Long userId = userContextService.currentUserId(authentication);
        return reservationService.searchReservationsForUser(userId, rs, minPrice, maxPrice, pageable);
    }

    private ReservationStatus parseReservationStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return ReservationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationDto updateStatus(@PathVariable Long id, @RequestParam String status) {
        ReservationStatus rs = parseReservationStatus(status);
        if (rs == null) {
            throw new BadRequestException("Status cannot be null");
        }
        return reservationService.updateStatus(id, rs);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        boolean admin = userContextService.isAdmin(authentication);
        if (admin) {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        }
        Long userId = userContextService.currentUserId(authentication);
        // perform atomic ownership-checked delete to avoid TOCTOU
        reservationService.deleteReservation(id, userId, false);
        return ResponseEntity.noContent().build();
    }
}

