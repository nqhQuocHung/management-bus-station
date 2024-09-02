package com.nqh.bus_station_management.bus_station.services.impl;

import com.nqh.bus_station_management.bus_station.dtos.CompanyDTO;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.repositories.CompanyRepository;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import com.nqh.bus_station_management.bus_station.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CompanyDTO> listCompanies(Map<String, String> params) {
        String name = params.get("name");
        List<TransportationCompany> companies = companyRepository.list(name);
        return companies.stream()
                .map(company -> new CompanyDTO(
                        company.getId(),
                        company.getName(),
                        company.getAvatar(),
                        company.getPhone(),
                        company.getEmail(),
                        company.getIsVerified(),
                        company.getIsActive(),
                        company.getIsCargoTransport(),
                        company.getManager().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Long countCompanies(Map<String, String> params) {
        String name = params.get("name");
        return companyRepository.count(name);
    }

    @Override
    public Optional<TransportationCompany> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public void saveCompany(CompanyDTO companyDTO) {
        TransportationCompany company = new TransportationCompany();
        company.setName(companyDTO.getName());
        company.setAvatar(companyDTO.getAvatar());
        company.setPhone(companyDTO.getPhone());
        company.setEmail(companyDTO.getEmail());
        company.setIsVerified(companyDTO.getIsVerified());
        company.setIsActive(companyDTO.getIsActive());
        company.setIsCargoTransport(companyDTO.getIsCargoTransport());

        if (companyDTO.getManagerId() != null) {
            Optional<User> manager = userRepository.findById(companyDTO.getManagerId());
            manager.ifPresent(company::setManager);
        }

        companyRepository.save(company);
    }

    @Override
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public void updateCompany(CompanyDTO companyDTO) {
        Optional<TransportationCompany> companyOptional = companyRepository.findById(companyDTO.getId());
        if (companyOptional.isPresent()) {
            TransportationCompany company = companyOptional.get();
            company.setName(companyDTO.getName());
            company.setAvatar(companyDTO.getAvatar());
            company.setPhone(companyDTO.getPhone());
            company.setEmail(companyDTO.getEmail());
            company.setIsVerified(companyDTO.getIsVerified());
            company.setIsActive(companyDTO.getIsActive());
            company.setIsCargoTransport(companyDTO.getIsCargoTransport());

            if (companyDTO.getManagerId() != null) {
                Optional<User> manager = userRepository.findById(companyDTO.getManagerId());
                manager.ifPresent(company::setManager);
            }

            companyRepository.save(company);
        }
    }

    @Override
    public List<CompanyDTO> getUnverifiedCompanies() {
        List<TransportationCompany> companies = companyRepository.findByIsVerifiedFalse();
        return companies.stream()
                .map(company -> new CompanyDTO(
                        company.getId(),
                        company.getName(),
                        company.getAvatar(),
                        company.getPhone(),
                        company.getEmail(),
                        company.getIsVerified(),
                        company.getIsActive(),
                        company.getIsCargoTransport(),
                        company.getManager().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void verifyCompany(Long id) {
        companyRepository.verifyCompany(id);
    }

    @Override
    public CompanyDTO getCompanyAndManager(Long companyId) {
        return companyRepository.getCompanyAndManager(companyId);
    }

    @Override
    public TransportationCompany findByManagerId(Long id) {
        return companyRepository.findByManagerId(id);
    }

    @Override
    public long countAllCompanies() {
        return companyRepository.countAllCompanies();
    }
}
