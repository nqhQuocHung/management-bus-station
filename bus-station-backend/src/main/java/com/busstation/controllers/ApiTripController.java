package com.busstation.controllers;

import com.busstation.dtos.TripRequestDTO;
import com.busstation.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trip")
public class ApiTripController {
    @Autowired
    TripService service;


    @GetMapping("/{id}/seat-details")
    public ResponseEntity<Object> handleSeatDetail(@PathVariable Long id) {
        return ResponseEntity.ok(service.seatDetails(id));
    }

    @PostMapping("/add")
    public ResponseEntity<String> createTrip(@RequestBody TripRequestDTO tripRequestDTO) {
        try {
            service.createTrip(tripRequestDTO);
            return ResponseEntity.ok("Trip created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error.");
        }
    }

}
