package com.busstation.repositories;

import com.busstation.pojo.Station;
import java.util.List;
import java.util.Optional;

public interface StationRepository {
    List<Station> getAll();
    Station findById(Long id);
    void save(Station station);
    void update(Station station);
    void deleteById(Long id);
    Optional<Station> getStationById(Long id);
}