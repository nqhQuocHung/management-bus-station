package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.*;
import com.nqh.bus_station_management.bus_station.pojo.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    Optional<Trip> getTripById(Long id);
    Trip createTrip(TripRegisterDTO tripRegisterDTO);
    TripDTO tripInfo(Long id);
    List<TripDTO> getTripsByRouteId(Long routeId);
    List<TripPublicDTO> getTripsByDriverId(Long driverId);
    Trip updateTripStatus(Long id, Boolean status);
    long getActiveTripCount();
    List<PassengerSeatDTO> getPassengersByTripId(Long tripId);
}
