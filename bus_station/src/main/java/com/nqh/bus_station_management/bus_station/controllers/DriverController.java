package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.DriverDTO;
import com.nqh.bus_station_management.bus_station.pojo.DriverCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.repositories.CompanyRepository;
import com.nqh.bus_station_management.bus_station.services.DriverCompanyService;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverCompanyService driverCompanyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository transportationCompanyRepository;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DriverDTO>> getDriversByCompanyId(@PathVariable Long companyId) {
        List<DriverDTO> drivers = driverCompanyService.getDriversByCompanyId(companyId);
        return ResponseEntity.ok(drivers);
    }

    @PostMapping("/create")
    public ResponseEntity<DriverCompany> createDriverCompany(@RequestParam Long userId, @RequestParam Long companyId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        TransportationCompany company = transportationCompanyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        DriverCompany createdDriverCompany = driverCompanyService.createDriverCompany(user, company);
        return ResponseEntity.ok(createdDriverCompany);
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<DriverCompany> verifyDriverCompany(@PathVariable Long id) {
        DriverCompany updatedDriverCompany = driverCompanyService.updateVerifiedStatus(id);
        return ResponseEntity.ok(updatedDriverCompany);
    }

    @GetMapping("/verified/company/{companyId}")
    public ResponseEntity<List<DriverDTO>> getVerifiedDriversByCompanyId(@PathVariable Long companyId) {
        List<DriverDTO> verifiedDrivers = driverCompanyService.getVerifiedDriversByCompanyId(companyId);
        return ResponseEntity.ok(verifiedDrivers);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverDTO>> getAvailableDrivers(
            @RequestParam(required = true) Long companyId,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            List<DriverDTO> drivers = driverCompanyService.getAvailableDriversByCompanyAndDate(companyId, date);
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

}
