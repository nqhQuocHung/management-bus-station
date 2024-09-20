package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.TripDTO;
import com.nqh.bus_station_management.bus_station.dtos.TripRegisterDTO;
import com.nqh.bus_station_management.bus_station.pojo.Seat;
import com.nqh.bus_station_management.bus_station.pojo.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    Optional<Trip> getTripById(Long id);
    Trip createTrip(TripRegisterDTO tripRegisterDTO);
    TripDTO tripInfo(Long id);
    public List<TripDTO> getTripsByRouteId(Long routeId);
}
