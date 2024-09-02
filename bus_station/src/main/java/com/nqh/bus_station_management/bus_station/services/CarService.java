package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.CarDTO;

import java.util.List;

public interface CarService {
    List<CarDTO> getCarsByCompanyId(Long companyId);
}
