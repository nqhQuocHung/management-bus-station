package com.busstation.services.impl;

import com.busstation.dtos.CarPublicDTO;
import com.busstation.pojo.Car;
import com.busstation.repositories.CarRepository;
import com.busstation.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Override
    public List<CarPublicDTO> getAvailableCarsByDate(Date date) {
        List<Car> cars = carRepository.findAvailableCarsByDate(date);
        return cars.stream().map(car -> new CarPublicDTO(car.getId(), car.getCarNumber())).collect(Collectors.toList());
    }
}
