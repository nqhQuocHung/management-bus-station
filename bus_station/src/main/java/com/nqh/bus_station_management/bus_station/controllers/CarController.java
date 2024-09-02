package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.CarDTO;
import com.nqh.bus_station_management.bus_station.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/company/{companyId}")
    public List<CarDTO> getCarsByCompanyId(@PathVariable Long companyId) {
        return carService.getCarsByCompanyId(companyId);
    }

    @GetMapping("/available")
    public List<CarDTO> getAvailableCarsByCompanyAndDate(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return carService.getAvailableCarsByCompanyAndDate(companyId, date);
    }
}
