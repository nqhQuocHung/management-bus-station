package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.CarDTO;
import com.nqh.bus_station_management.bus_station.pojo.Car;

import java.util.Date;
import java.util.List;

public interface CarService {
    List<CarDTO> getCarsByCompanyId(Long companyId);
    List<CarDTO> getAvailableCarsByCompanyAndDate(Long busStationId, Date date);
    Car createCar(String carNumber, Long companyId);
}
