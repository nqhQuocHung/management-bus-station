package com.busstation.controllers;

import com.busstation.dtos.CarPublicDTO;
import com.busstation.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class ApiCarController {

    @Autowired
    private CarService carService;

    @GetMapping("/available-cars")
    public List<CarPublicDTO> getAvailableCars(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return carService.getAvailableCarsByDate(date);
    }
}
