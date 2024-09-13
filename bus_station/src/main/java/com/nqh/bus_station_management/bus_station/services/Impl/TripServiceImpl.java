package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.TripDTO;
import com.nqh.bus_station_management.bus_station.mappers.TripDTOMapper;
import com.nqh.bus_station_management.bus_station.pojo.Seat;
import com.nqh.bus_station_management.bus_station.pojo.Trip;
import com.nqh.bus_station_management.bus_station.repositories.TripRepository;
import com.nqh.bus_station_management.bus_station.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripDTOMapper tripDTOMapper;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository, TripDTOMapper tripDTOMapper) {
        this.tripRepository = tripRepository;
        this.tripDTOMapper = tripDTOMapper;
    }

    @Override
    public List<Seat> getAvailableSeats(Long tripId) {
        return tripRepository.getAvailableSeats(tripId);
    }

    @Override
    public List<Seat> getUnAvailableSeats(Long tripId) {
        return tripRepository.getUnAvailableSeats(tripId);
    }

    @Override
    public Optional<Seat> findAvailableSeat(Long tripId, Long seatId) {
        return Optional.ofNullable(tripRepository.availableSeat(tripId, seatId));
    }

    @Override
    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    @Override
    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    @Override
    public TripDTO tripInfo(Long id) {
        Trip trip = tripRepository.getById(id);
        return tripDTOMapper.apply(trip);
    }
}
