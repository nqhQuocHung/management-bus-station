package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final TicketService ticketService;

    @Autowired
    public StatisticsController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/annual")
    public ResponseEntity<List<StatisticsDTO>> calculateAnnualRevenue(@RequestParam int year, @RequestParam Long companyId) {
        List<StatisticsDTO> statistics = ticketService.calculateAnnualRevenue(year, companyId);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/quarterly")
    public ResponseEntity<List<StatisticsDTO>> calculateQuarterlyRevenue(@RequestParam int year, @RequestParam Long companyId) {
        List<StatisticsDTO> statistics = ticketService.calculateQuarterlyRevenue(year, companyId);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<StatisticsDTO>> calculateDailyRevenue(@RequestParam int year, @RequestParam int month, @RequestParam int day, @RequestParam Long companyId) {
        List<StatisticsDTO> statistics = ticketService.calculateDailyRevenue(year, month, day, companyId);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}
