package com.busstation.mappers;

import com.busstation.dtos.TripDTO;
import com.busstation.pojo.Seat;
import com.busstation.pojo.Trip;
import com.busstation.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
