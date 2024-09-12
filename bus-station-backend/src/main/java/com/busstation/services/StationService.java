
package com.busstation.services;

import com.busstation.pojo.Station;
import java.util.List;

public interface StationService {
    List<Station> getAllStations();
    Station getStationById(Long id);
    void addStation(Station station);
    void updateStation(Station station);
    void deleteStationById(Long id);
}
