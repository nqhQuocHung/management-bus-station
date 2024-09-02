package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.pojo.Seat;
import com.nqh.bus_station_management.bus_station.pojo.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    List<Seat> getAvailableSeats(Long tripId);
    List<Seat> getUnAvailableSeats(Long tripId);
    Optional<Seat> findAvailableSeat(Long tripId, Long seatId);
    Optional<Trip> getTripById(Long id);
    Trip saveTrip(Trip trip);
}
