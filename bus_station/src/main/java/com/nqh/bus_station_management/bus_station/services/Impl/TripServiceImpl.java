package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.*;
import com.nqh.bus_station_management.bus_station.mappers.PassengerSeatDTOMapper;
import com.nqh.bus_station_management.bus_station.mappers.TripDTOMapper;
import com.nqh.bus_station_management.bus_station.pojo.*;
import com.nqh.bus_station_management.bus_station.repositories.*;
import com.nqh.bus_station_management.bus_station.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.ZonedDateTime;
import java.time.ZoneId;

@Service
@Transactional
public class TripServiceImpl implements TripService {

    @Autowired
    private  TripRepository tripRepository;

    @Autowired
    private  TripDTOMapper tripDTOMapper;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;


    @Override
    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    @Override
    public Trip createTrip(TripRegisterDTO tripRegisterDTO) {
        Optional<Car> car = carRepository.findById(tripRegisterDTO.getCarId());
        Optional<Route> route = routeRepository.findById(tripRegisterDTO.getRouteId());
        Optional<User> driver = userRepository.findById(tripRegisterDTO.getDriverId());

        if (car.isEmpty() || route.isEmpty() || driver.isEmpty()) {
            throw new RuntimeException("Car, Route, or Driver not found");
        }

        LocalDateTime localDateTime = tripRegisterDTO.getDepartAt().toLocalDateTime();

        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        Trip trip = Trip.builder()
                .car(car.get())
                .route(route.get())
                .driver(driver.get())
                .departAt(Timestamp.valueOf(utcDateTime.toLocalDateTime()))
                .isActive(tripRegisterDTO.getIsActive())
                .status(false)
                .build();

        return tripRepository.save(trip);
    }

    @Override
    public TripDTO tripInfo(Long id) {
        Trip trip = tripRepository.getById(id);
        return tripDTOMapper.apply(trip);
    }

    @Override
    public List<TripDTO> getTripsByRouteId(Long routeId) {
        List<Trip> trips = tripRepository.findByRouteId(routeId);
        return trips.stream()
                .filter(trip -> trip.getDepartAt().toLocalDateTime().isAfter(LocalDateTime.now()))
                .map(tripDTOMapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripPublicDTO> getTripsByDriverId(Long driverId) {
        List<Trip> trips = tripRepository.findByDriverId(driverId);
        return trips.stream()
                .map(this::mapToTripPublicDTO)
                .collect(Collectors.toList());
    }

    private TripPublicDTO mapToTripPublicDTO(Trip trip) {
        return new TripPublicDTO(
                trip.getId(),
                trip.getRoute().getName(),
                trip.getRoute().getCargoPrice(),
                trip.getRoute().getSeatPrice(),
                trip.getCar().getCarNumber(),
                trip.getDepartAt(),
                trip.getStatus()
        );
    }

    @Override
    public Trip updateTripStatus(Long id, Boolean status) {
        Optional<Trip> tripOptional = tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();
            trip.setStatus(status);
            return tripRepository.save(trip);
        } else {
            throw new RuntimeException("Trip not found with id: " + id);
        }
    }

    @Override
    public long getActiveTripCount() {
        return tripRepository.countActiveTrips();
    }

    @Override
    public List<PassengerSeatDTO> getPassengersByTripId(Long tripId) {
        List<Ticket> tickets = ticketRepository.findByTripId(tripId);
        return tickets.stream()
                .map(PassengerSeatDTOMapper::mapToPassengerSeatDTO)
                .collect(Collectors.toList());
    }

}
