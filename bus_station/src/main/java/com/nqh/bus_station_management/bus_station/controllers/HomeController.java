package com.nqh.bus_station_management.bus_station.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Bus Station Nguyen Quoc Hung!";
    }
}
