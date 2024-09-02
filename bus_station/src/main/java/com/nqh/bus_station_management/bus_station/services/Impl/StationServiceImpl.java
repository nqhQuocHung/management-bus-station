package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.pojo.Station;
import com.nqh.bus_station_management.bus_station.repositories.StationRepository;
import com.nqh.bus_station_management.bus_station.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    @Autowired
    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Override
    public Optional<Station> getStationById(Long id) {
        return stationRepository.findById(id);
    }

    @Override
    public Station saveStation(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public void updateStation(Station station) {
        stationRepository.save(station);
    }

    @Override
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
