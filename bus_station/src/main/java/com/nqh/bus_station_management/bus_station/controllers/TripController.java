package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.TripDTO;
import com.nqh.bus_station_management.bus_station.dtos.TripRegisterDTO;
import com.nqh.bus_station_management.bus_station.pojo.Seat;
import com.nqh.bus_station_management.bus_station.pojo.Trip;
import com.nqh.bus_station_management.bus_station.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/{id}")
    public Optional<Trip> getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    @GetMapping("/{id}/available-seats")
    public List<Seat> getAvailableSeats(@PathVariable Long id) {
        return tripService.getAvailableSeats(id);
    }

    @GetMapping("/{id}/unavailable-seats")
    public List<Seat> getUnAvailableSeats(@PathVariable Long id) {
        return tripService.getUnAvailableSeats(id);
    }

    @GetMapping("/{tripId}/seat/{seatId}")
    public Optional<Seat> findAvailableSeat(@PathVariable Long tripId, @PathVariable Long seatId) {
        return tripService.findAvailableSeat(tripId, seatId);
    }

    @PostMapping("/create")
    public ResponseEntity<Trip> createTrip(@RequestBody TripRegisterDTO tripRegisterDTO) {
        Trip newTrip = tripService.createTrip(tripRegisterDTO);
        return ResponseEntity.ok(newTrip);
    }
}
