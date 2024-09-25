package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.SeatPublicDTO;
import com.nqh.bus_station_management.bus_station.pojo.Seat;
import com.nqh.bus_station_management.bus_station.repositories.SeatRepository;
import com.nqh.bus_station_management.bus_station.repositories.TripRepository;
import com.nqh.bus_station_management.bus_station.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SeatServiceImpl implements SeatService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TripRepository tripRepository;

    @Override
    public List<SeatPublicDTO> getAvailableSeats(Long tripId) {
        Long carId = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"))
                .getCar().getId();

        List<Seat> availableSeats = seatRepository.findAvailableSeatsByTripId(tripId, carId);
        return availableSeats.stream()
                .map(seat -> new SeatPublicDTO(seat.getId(), seat.getSeatCode().getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatPublicDTO> getOccupiedSeats(Long tripId) {
        Long carId = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"))
                .getCar().getId();

        List<Seat> occupiedSeats = seatRepository.findOccupiedSeatsByTripId(tripId, carId);
        return occupiedSeats.stream()
                .map(seat -> new SeatPublicDTO(seat.getId(), seat.getSeatCode().getCode()))
                .collect(Collectors.toList());
    }
}
