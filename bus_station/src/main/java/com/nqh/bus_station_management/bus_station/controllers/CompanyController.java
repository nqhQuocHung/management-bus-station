package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.CompanyDTO;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public List<CompanyDTO> listCompanies(@RequestParam Map<String, String> params) {
        return companyService.listCompanies(params);
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

    @PostMapping
    public void saveCompany(@RequestBody CompanyDTO companyDTO) {
        companyService.saveCompany(companyDTO);
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

    @GetMapping("/manager/{companyId}")
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
}
