package com.nqh.bus_station_management.bus_station.mappers;

import com.nqh.bus_station_management.bus_station.dtos.TripDTO;
import com.nqh.bus_station_management.bus_station.pojo.Trip;
import org.springframework.stereotype.Service;


import java.util.function.Function;

@Service
public class TripDTOMapper implements Function<Trip, TripDTO> {


    @Override
    public TripDTO apply(Trip trip) {
        return TripDTO.builder()
                .id(trip.getId())
                .departAt(trip.getDepartAt())
                .build();
    }
}
