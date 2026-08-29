package com.example.demo.controller;

import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authorizationCheck")
public class AuthorizationCheck {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public AuthorizationCheck(ReservationRepository reservationRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Returns true when the authenticated principal owns the given reservation id.
     * Safe to call from SpEL in @PreAuthorize expressions.
     */
    public boolean isReservationOwner(Long reservationId, Authentication authentication) {
        if (authentication == null || reservationId == null) return false;
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .flatMap(u -> reservationRepository.findById(reservationId)
                        .map(r -> r.getUser() != null && r.getUser().getId().equals(u.getId())))
                .orElse(false);
    }
}
