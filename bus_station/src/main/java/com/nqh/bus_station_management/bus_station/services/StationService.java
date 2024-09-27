package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.pojo.Station;

import java.util.List;
import java.util.Optional;

public interface StationService {
    List<Station> getAllStations();
    Optional<Station> getStationById(Long id);
    Station saveStation(Station station);
    void updateStation(Station station);
    void deleteStationById(Long id);
    long getTotalStationCount();
}
