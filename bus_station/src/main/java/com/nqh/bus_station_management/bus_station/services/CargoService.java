package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.pojo.Cargo;

public interface CargoService {
    Cargo createCargo(Cargo cargo);
    void deleteCargoById(Long id);
}
