package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.SeatPublicDTO;
import com.nqh.bus_station_management.bus_station.pojo.Seat;

import java.util.List;

public interface SeatService {
    List<SeatPublicDTO> getAvailableSeats(Long tripId);

    List<SeatPublicDTO> getOccupiedSeats(Long tripId);

    List<Seat> createSeatsForCar(Long carId);
}
