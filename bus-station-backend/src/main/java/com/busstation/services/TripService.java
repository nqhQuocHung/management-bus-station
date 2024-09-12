package com.busstation.services;

import com.busstation.dtos.TripDTO;
import com.busstation.dtos.TripRequestDTO;
import com.busstation.pojo.Seat;
import com.busstation.pojo.Trip;

import java.util.Map;

public interface TripService {
    Map<String, Object> seatDetails(Long id);
    TripDTO tripInfo(Long id);
    Trip getById(Long id);
    Seat availableSeat(Long tripId, Long seatId);
    void createTrip(TripRequestDTO tripRequestDTO);
}
