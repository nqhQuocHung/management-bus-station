package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.CarDTO;
import com.nqh.bus_station_management.bus_station.pojo.Car;
import com.nqh.bus_station_management.bus_station.repositories.CarRepository;
import com.nqh.bus_station_management.bus_station.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<CarDTO> getCarsByCompanyId(Long companyId) {
        return carRepository.findCarsByCompanyId(companyId)
                .stream()
                .map(car -> new CarDTO(car.getId(), car.getCarNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CarDTO> getAvailableCarsByCompanyAndDate(Long busStationId, Date date) {
        List<Car> cars = carRepository.findAvailableCarsByCompanyAndDate(busStationId, date);
        return cars.stream().map(car -> CarDTO.builder()
                .id(car.getId())
                .carNumber(car.getCarNumber())
                .build()).collect(Collectors.toList());
    }
}
