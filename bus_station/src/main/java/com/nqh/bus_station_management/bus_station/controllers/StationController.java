package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.pojo.Station;
import com.nqh.bus_station_management.bus_station.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private  StationService stationService;


    @GetMapping("/list")
    public List<Station> getAllStations() {
        return stationService.getAllStations();
    }

    @GetMapping("/{id}")
    public Optional<Station> getStationById(@PathVariable Long id) {
        return stationService.getStationById(id);
    }

    @PostMapping
    public Station saveStation(@RequestBody Station station) {
        return stationService.saveStation(station);
    }

    @PutMapping("/{id}")
    public void updateStation(@RequestBody Station station, @PathVariable Long id) {
        station.setId(id);
        stationService.updateStation(station);
    }

    @DeleteMapping("/{id}")
    public void deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
    }
}
