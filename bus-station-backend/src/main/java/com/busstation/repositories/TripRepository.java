package com.busstation.repositories;

import com.busstation.dtos.TripDTO;
import com.busstation.pojo.Seat;
import com.busstation.pojo.Trip;

import java.util.List;

public interface TripRepository {
    List<Seat> getAvailableSeats(Long id);
    List<Seat> getUnAvailableSeats(Long id);
    Seat availableSeat(Long tripId, Long seatId);
    Trip getById(Long id);
    Trip save(Trip trip);
}
