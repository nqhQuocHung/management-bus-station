package com.busstation.controllers;

import com.busstation.pojo.Station;
import com.busstation.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
public class ApiStationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/list")
    public ResponseEntity<List<Station>> getAllStations() {
        List<Station> stations = stationService.getAllStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> retrieve(@PathVariable Long id) {
        Station station = stationService.getStationById(id);
        return ResponseEntity.ok(station);
    }

    @PostMapping("/add")
    public ResponseEntity<Station> add(@RequestBody Station station) {
        stationService.addStation(station);
        return ResponseEntity.ok(station);
    }

    @PutMapping("/update")
    public ResponseEntity<Station> update(@RequestBody Station station) {
        stationService.updateStation(station);
        return ResponseEntity.ok(station);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
