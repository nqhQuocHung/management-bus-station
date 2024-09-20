package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.pojo.Cargo;
import com.nqh.bus_station_management.bus_station.repositories.CargoRepository;
import com.nqh.bus_station_management.bus_station.services.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoServiceImpl implements CargoService {

    @Autowired
    private CargoRepository cargoRepository;

    @Override
    public Cargo createCargo(Cargo cargo) {
        return cargoRepository.save(cargo);
    }

    @Override
    public void deleteCargoById(Long id) {
        if (cargoRepository.existsById(id)) {
            cargoRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Cargo với ID " + id + " không tồn tại.");
        }
    }
}

