package com.example.demo.mapper;

import com.example.demo.dto.ReservationDto;
import com.example.demo.entity.Reservation;

public class ReservationMapper {

    public static ReservationDto toDto(Reservation r) {
        if (r == null) return null;
        ReservationDto d = new ReservationDto();
        d.setId(r.getId());
        d.setResourceId(r.getResource() != null ? r.getResource().getId() : null);
        d.setUserId(r.getUser() != null ? r.getUser().getId() : null);
        d.setStartTime(r.getStartTime());
        d.setEndTime(r.getEndTime());
        d.setPrice(r.getPrice());
        d.setStatus(r.getStatus());
        return d;
    }

}
