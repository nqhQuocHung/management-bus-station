package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final TicketService ticketService;

    @Autowired
    public StatisticsController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/annual/{year}")
    public ResponseEntity<List<StatisticsDTO>> calculateAnnualRevenue(
            @PathVariable int year,
            @RequestParam Long companyId) {
        List<StatisticsDTO> statistics = ticketService.calculateAnnualRevenue(year, companyId);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/quarterly/{year}")
    public ResponseEntity<List<StatisticsDTO>> calculateQuarterlyRevenue(@PathVariable int year, @RequestParam Long companyId) {
        List<StatisticsDTO> statistics = ticketService.calculateQuarterlyRevenue(year, companyId);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/daily/{year}/{month}/{day}")
    public ResponseEntity<List<StatisticsDTO>> calculateDailyRevenue(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int day,
            @RequestParam Long companyId) {
        List<StatisticsDTO> statistics = ticketService.calculateDailyRevenue(year, month, day, companyId);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

}
