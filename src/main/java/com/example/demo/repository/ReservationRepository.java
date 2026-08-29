package com.example.demo.repository;

import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    // Legacy derived query methods remain for compatibility. Prefer Specification-based searches.
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    // Atomic delete that ensures ownership: returns number of rows deleted (0 or 1)
    long deleteByIdAndUserId(Long id, Long userId);
}

