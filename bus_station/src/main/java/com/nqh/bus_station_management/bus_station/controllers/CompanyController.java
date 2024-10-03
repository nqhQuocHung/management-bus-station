package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.CompanyDTO;
import com.nqh.bus_station_management.bus_station.dtos.CompanyPublicDTO;
import com.nqh.bus_station_management.bus_station.dtos.CompanyRegisterDTO;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.services.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    @Autowired
    private  CompanyService companyService;

    @GetMapping("/verified")
    public Map<String, Object> listVerifiedCompanies(@RequestParam Map<String, String> params) {
        return companyService.listVerifiedCompanies(params);
    }

    @GetMapping
    public Map<String, Object> listCompanies(@RequestParam Map<String, String> params) {
        return companyService.listCompanies(params);
    }

    @GetMapping("/name-id")
    public List<CompanyPublicDTO> getNameId() {
        return companyService.getNameId();
    }

    @GetMapping("/count")
    public Long countCompanies(@RequestParam Map<String, String> params) {
        return companyService.countCompanies(params);
    }

    @GetMapping("/{id}")
    public Optional<CompanyDTO> getCompanyById(@PathVariable Long id) {
        Optional<TransportationCompany> company = companyService.getCompanyById(id);
        return company.map(value -> new CompanyDTO(
                value.getId(),
                value.getName(),
                value.getAvatar(),
                value.getPhone(),
                value.getEmail(),
                value.getIsVerified(),
                value.getIsActive(),
                value.getIsCargoTransport(),
                value.getManager().getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<TransportationCompany> createCompany(@Valid @RequestBody CompanyRegisterDTO companyDTO) {
        TransportationCompany company = companyService.createCompany(companyDTO);
        return new ResponseEntity<>(company, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompanyById(id);
    }

    @PutMapping("/{id}")
    public void updateCompany(@RequestBody CompanyDTO companyDTO, @PathVariable Long id) {
        companyDTO.setId(id);
        companyService.updateCompany(companyDTO);
    }

    @GetMapping("/unverified")
    public List<CompanyDTO> getUnverifiedCompanies() {
        return companyService.getUnverifiedCompanies();
    }

    @PutMapping("/verify/{id}")
    public void verifyCompany(@PathVariable Long id) {
        companyService.verifyCompany(id);
    }

    @GetMapping("/manager/company/{companyId}")
    public CompanyDTO getCompanyAndManager(@PathVariable Long companyId) {
        return companyService.getCompanyAndManager(companyId);
    }

    @GetMapping("/manager/{id}")
    public TransportationCompany findByManagerId(@PathVariable Long id) {
        return companyService.findByManagerId(id);
    }

    @GetMapping("/count/all")
    public long countAllCompanies() {
        return companyService.countAllCompanies();
    }

    @PatchMapping("/cargo/{companyId}")
    public ResponseEntity<String> toggleCargoTransport(@PathVariable Long companyId) {
        try {
            companyService.toggleCargoTransport(companyId);
            return ResponseEntity.ok("Cargo transport status toggled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to toggle cargo transport status.");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companyList = companyService.getAllCompanies();
        return ResponseEntity.ok(companyList);
    }
}
