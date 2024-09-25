package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.SeatPublicDTO;
import com.nqh.bus_station_management.bus_station.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private  SeatService seatService;

    @GetMapping("/available")
    public List<SeatPublicDTO> getAvailableSeats(@RequestParam Long tripId) {
        return seatService.getAvailableSeats(tripId);
    }

    @GetMapping("/occupied/{tripId}")
    public ResponseEntity<List<SeatPublicDTO>> getOccupiedSeats(@PathVariable Long tripId) {
        List<SeatPublicDTO> seats = seatService.getOccupiedSeats(tripId);
        return new ResponseEntity<>(seats, HttpStatus.OK);
    }
}
