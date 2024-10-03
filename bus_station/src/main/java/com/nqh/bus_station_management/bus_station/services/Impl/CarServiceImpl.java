package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.CarDTO;
import com.nqh.bus_station_management.bus_station.pojo.Car;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.repositories.CarRepository;
import com.nqh.bus_station_management.bus_station.repositories.CompanyRepository;
import com.nqh.bus_station_management.bus_station.services.CarService;
import com.nqh.bus_station_management.bus_station.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SeatService seatService;

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

    @Override
    public Car createCar(String carNumber, Long companyId) {
        TransportationCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company with ID " + companyId + " not found"));
        Car car = new Car();
        car.setCarNumber(carNumber);
        car.setCompany(company);
        Car savedCar = carRepository.save(car);
        seatService.createSeatsForCar(savedCar.getId());

        return savedCar;
    }
}
