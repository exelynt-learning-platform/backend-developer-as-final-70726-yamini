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
import com.example.demo.mapper.ReservationMapper;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.ResourceRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, ResourceRepository resourceRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    public ReservationDto createReservation(ReservationDto dto, Long userId) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new BadRequestException("startTime and endTime are required");
        }
        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new BadRequestException("endTime must be after startTime");
        }

        Resource resource = resourceRepository.findById(dto.getResourceId()).orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + dto.getResourceId()));
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("User not found: " + userId));

        Reservation r = new Reservation();
        r.setResource(resource);
        r.setUser(user);
        r.setStartTime(dto.getStartTime());
        r.setEndTime(dto.getEndTime());

        // Use the resource's configured price to prevent client-side price manipulation.
        BigDecimal resourcePrice = resource.getPrice();
        if (resourcePrice == null || resourcePrice.signum() < 0) {
            throw new BadRequestException("Resource has invalid price");
        }
        r.setPrice(resourcePrice);
        r.setStatus(ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(r);
        return ReservationMapper.toDto(saved);
    }

    public ReservationDto getReservation(Long id, Long requesterId, boolean isAdmin) {
        Reservation r = reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id));
        if (!isAdmin && !r.getUser().getId().equals(requesterId)) {
            throw new UnauthorizedException("Access denied");
        }
        return ReservationMapper.toDto(r);
    }

    public Page<ReservationDto> searchReservationsForAdmin(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (status != null && minPrice != null && maxPrice != null) {
            return reservationRepository.findAllByStatusAndPriceBetween(status, minPrice, maxPrice, pageable).map(ReservationMapper::toDto);
        }

        if (status != null && minPrice != null && maxPrice == null) {
            return reservationRepository.findAllByStatusAndPriceGreaterThanEqual(status, minPrice, pageable).map(ReservationMapper::toDto);
        }

        if (status != null && maxPrice != null && minPrice == null) {
            return reservationRepository.findAllByStatusAndPriceLessThanEqual(status, maxPrice, pageable).map(ReservationMapper::toDto);
        }

        if (status != null) {
            return reservationRepository.findAllByStatus(status, pageable).map(ReservationMapper::toDto);
        }

        if (minPrice != null && maxPrice != null) {
            return reservationRepository.findAllByPriceBetween(minPrice, maxPrice, pageable).map(ReservationMapper::toDto);
        }

        if (minPrice != null) {
            return reservationRepository.findAllByPriceGreaterThanEqual(minPrice, pageable).map(ReservationMapper::toDto);
        }

        if (maxPrice != null) {
            return reservationRepository.findAllByPriceLessThanEqual(maxPrice, pageable).map(ReservationMapper::toDto);
        }

        return reservationRepository.findAll(pageable).map(ReservationMapper::toDto);
    }

    public Page<ReservationDto> searchReservationsForUser(Long userId, ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (status != null && minPrice != null && maxPrice != null) {
            return reservationRepository.findByUserIdAndStatusAndPriceBetween(userId, status, minPrice, maxPrice, pageable).map(ReservationMapper::toDto);
        }

        if (status != null && minPrice != null && maxPrice == null) {
            return reservationRepository.findByUserIdAndStatusAndPriceGreaterThanEqual(userId, status, minPrice, pageable).map(ReservationMapper::toDto);
        }

        if (status != null && maxPrice != null && minPrice == null) {
            return reservationRepository.findByUserIdAndStatusAndPriceLessThanEqual(userId, status, maxPrice, pageable).map(ReservationMapper::toDto);
        }

        if (status != null) {
            return reservationRepository.findByUserIdAndStatus(userId, status, pageable).map(ReservationMapper::toDto);
        }

        if (minPrice != null && maxPrice != null) {
            return reservationRepository.findByUserIdAndPriceBetween(userId, minPrice, maxPrice, pageable).map(ReservationMapper::toDto);
        }

        if (minPrice != null) {
            return reservationRepository.findByUserIdAndPriceGreaterThanEqual(userId, minPrice, pageable).map(ReservationMapper::toDto);
        }

        if (maxPrice != null) {
            return reservationRepository.findByUserIdAndPriceLessThanEqual(userId, maxPrice, pageable).map(ReservationMapper::toDto);
        }

        return reservation_repository_find_all_for_user(userId, pageable);
    }

    // fallback helper to use existing method name
    private Page<ReservationDto> reservation_repository_find_all_for_user(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable).map(ReservationMapper::toDto);
    }

    public ReservationDto updateStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation r = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + reservationId));
        r.setStatus(newStatus);
        Reservation saved = reservationRepository.save(r);
        return ReservationMapper.toDto(saved);
    }

    public void deleteReservation(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ReservationNotFoundException("Reservation not found: " + reservationId);
        }
        reservationRepository.deleteById(reservationId);
    }
}

