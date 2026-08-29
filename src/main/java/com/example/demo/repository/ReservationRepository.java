package com.example.demo.repository;

import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Admin queries
    Page<Reservation> findAllByStatus(ReservationStatus status, Pageable pageable);

    Page<Reservation> findAllByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Reservation> findAllByPriceGreaterThanEqual(BigDecimal minPrice, Pageable pageable);

    Page<Reservation> findAllByPriceLessThanEqual(BigDecimal maxPrice, Pageable pageable);

    Page<Reservation> findAllByStatusAndPriceBetween(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Reservation> findAllByStatusAndPriceGreaterThanEqual(ReservationStatus status, BigDecimal minPrice, Pageable pageable);
    Page<Reservation> findAllByStatusAndPriceLessThanEqual(ReservationStatus status, BigDecimal maxPrice, Pageable pageable);

    // User-scoped queries
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    Page<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status, Pageable pageable);

    Page<Reservation> findByUserIdAndPriceBetween(Long userId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Reservation> findByUserIdAndPriceGreaterThanEqual(Long userId, BigDecimal minPrice, Pageable pageable);

    Page<Reservation> findByUserIdAndPriceLessThanEqual(Long userId, BigDecimal maxPrice, Pageable pageable);

    Page<Reservation> findByUserIdAndStatusAndPriceBetween(Long userId, ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Reservation> findByUserIdAndStatusAndPriceGreaterThanEqual(Long userId, ReservationStatus status, BigDecimal minPrice, Pageable pageable);
    Page<Reservation> findByUserIdAndStatusAndPriceLessThanEqual(Long userId, ReservationStatus status, BigDecimal maxPrice, Pageable pageable);
}
package com.example.demo.repository;

public class ReservationRepository {

}
