package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.CompanyDTO;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CompanyService {

    List<CompanyDTO> listCompanies(Map<String, String> params);

    Long countCompanies(Map<String, String> params);

    Optional<TransportationCompany> getCompanyById(Long id);

    void saveCompany(CompanyDTO companyDTO);

    void deleteCompanyById(Long id);

    void updateCompany(CompanyDTO companyDTO);

    List<CompanyDTO> getUnverifiedCompanies();

    void verifyCompany(Long id);

    CompanyDTO getCompanyAndManager(Long companyId);

    TransportationCompany findByManagerId(Long id);

    long countAllCompanies();
}
