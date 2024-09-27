package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsAdminDTO;
import com.nqh.bus_station_management.bus_station.dtos.StatisticsBarDTO;
import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.dtos.StatisticsUserDTO;
import com.nqh.bus_station_management.bus_station.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private  TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private TripService tripService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private StationService stationService;

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

    @GetMapping("/user-statistics")
    public ResponseEntity<List<StatisticsUserDTO>> getUserStatistics() {
        List<StatisticsUserDTO> userStatistics = userService.getUserStatistics();
        return ResponseEntity.ok(userStatistics);
    }

    @GetMapping("/annual-revenue")
    public ResponseEntity<List<StatisticsAdminDTO>> getAnnualRevenue(@RequestParam("year") int year) {
        List<StatisticsAdminDTO> statistics = ticketService.calculateMonthlyRevenueAdmin(year);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/bar-data")
    public List<StatisticsBarDTO> getStatisticsBarData() {
        StatisticsBarDTO activeTripCount = new StatisticsBarDTO("Active Trips", tripService.getActiveTripCount());
        StatisticsBarDTO totalStationCount = new StatisticsBarDTO("Total Stations", stationService.getTotalStationCount());
        StatisticsBarDTO verifiedCompanyCount = new StatisticsBarDTO("Verified Companies", companyService.getVerifiedCompanyCount());
        StatisticsBarDTO activeCompanyCount = new StatisticsBarDTO("Active Companies", companyService.getActiveCompanyCount());

        return Arrays.asList(activeTripCount, totalStationCount, verifiedCompanyCount, activeCompanyCount);
    }
}
