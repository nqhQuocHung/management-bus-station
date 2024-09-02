package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<Integer, StatisticsDTO>> getAnnualRevenue(
            @RequestParam int year,
            @RequestParam Long companyId) {
        Map<Integer, StatisticsDTO> statistics = ticketService.calculateAnnualRevenue(year, companyId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/quarterly")
    public ResponseEntity<Map<Integer, StatisticsDTO>> getQuarterlyRevenue(
            @RequestParam int year,
            @RequestParam Long companyId) {
        Map<Integer, StatisticsDTO> statistics = ticketService.calculateQuarterlyRevenue(year, companyId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<Integer, StatisticsDTO>> getDailyRevenue(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day,
            @RequestParam Long companyId) {
        Map<Integer, StatisticsDTO> statistics = ticketService.calculateDailyRevenue(year, month, day, companyId);
        return ResponseEntity.ok(statistics);
    }
}
