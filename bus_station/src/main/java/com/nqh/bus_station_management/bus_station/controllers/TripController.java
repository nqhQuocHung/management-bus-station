package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.*;
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

    @Autowired
    private  TripService tripService;


    @GetMapping("/{id}")
    public Optional<Trip> getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }


    @PostMapping("/create")
    public ResponseEntity<Trip> createTrip(@RequestBody TripRegisterDTO tripRegisterDTO) {
        Trip newTrip = tripService.createTrip(tripRegisterDTO);
        return ResponseEntity.ok(newTrip);
    }

    @GetMapping("/route/{routeId}")
    public List<TripDTO> getTripsByRouteId(@PathVariable Long routeId) {
        return tripService.getTripsByRouteId(routeId);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<TripPublicDTO>> getTripsByDriverId(@PathVariable Long driverId) {
        List<TripPublicDTO> trips = tripService.getTripsByDriverId(driverId);
        if (trips.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(trips);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Trip> updateTripStatus(@PathVariable Long id) {
        Trip updatedTrip = tripService.updateTripStatus(id, true);
        return ResponseEntity.ok(updatedTrip);
    }

    @GetMapping("/{tripId}/passengers")
    public ResponseEntity<List<PassengerSeatDTO>> getPassengersByTripId(@PathVariable Long tripId) {
        List<PassengerSeatDTO> passengers = tripService.getPassengersByTripId(tripId);
        return ResponseEntity.ok(passengers);
    }
}
