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
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    public ReservationController(ReservationService reservationService, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    private Long currentUserId(Authentication authentication) {
        String email = authentication == null ? null : authentication.getName();
        if (email == null) throw new UnauthorizedException("Unauthenticated");
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found: " + email)).getId();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ReservationDto> create(@RequestBody ReservationDto dto, Authentication authentication) {
        Long userId = currentUserId(authentication);
        ReservationDto created = reservationService.createReservation(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ReservationDto get(@PathVariable Long id, Authentication authentication) {
        Long requesterId = currentUserId(authentication);
        boolean admin = isAdmin(authentication);
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
        ReservationStatus rs = null;
        if (status != null) {
            try {
                rs = ReservationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid status: " + status);
            }
        }

        Sort sortObj = Sort.unsorted();
        if (sort != null) {
            // expected format: property,asc|desc
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                Sort.Direction dir = Sort.Direction.fromString(parts[1]);
                sortObj = Sort.by(dir, parts[0]);
            } else {
                sortObj = Sort.by(sort);
            }
        }
        PageRequest pageable = PageRequest.of(page, size, sortObj);

        boolean admin = isAdmin(authentication);
        if (admin) {
            return reservationService.searchReservationsForAdmin(rs, minPrice, maxPrice, pageable);
        }
        Long userId = currentUserId(authentication);
        return reservationService.searchReservationsForUser(userId, rs, minPrice, maxPrice, pageable);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationDto updateStatus(@PathVariable Long id, @RequestParam String status) {
        ReservationStatus rs;
        try {
            rs = ReservationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status: " + status);
        }
        return reservationService.updateStatus(id, rs);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        boolean admin = isAdmin(authentication);
        if (admin) {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        }
        Long userId = currentUserId(authentication);
        // ensure ownership
        reservationService.getReservation(id, userId, false);
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}

