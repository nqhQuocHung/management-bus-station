package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.DriverDTO;
import com.nqh.bus_station_management.bus_station.pojo.DriverCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.pojo.Role;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.repositories.DriverCompanyRepository;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import com.nqh.bus_station_management.bus_station.repositories.RoleRepository;
import com.nqh.bus_station_management.bus_station.services.DriverCompanyService;
import com.nqh.bus_station_management.bus_station.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverCompanyServiceImpl implements DriverCompanyService {
    @Autowired
    private  DriverCompanyRepository driverCompanyRepository;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  RoleRepository roleRepository;

    @Autowired
    EmailService emailService;

    @Override
    @Transactional
    public DriverCompany createDriverCompany(User user, TransportationCompany company) {
        if (driverCompanyRepository.existsByUserIdAndCompanyId(user.getId(), company.getId())) {
            throw new IllegalArgumentException("Tài xế đã đăng ký làm việc với công ty này.");
        }
        DriverCompany driverCompany = DriverCompany.builder()
                .user(user)
                .company(company)
                .verified(false)
                .build();
        return driverCompanyRepository.save(driverCompany);
    }

    @Override
    @Transactional
    public DriverCompany updateVerifiedStatus(Long userId) {
        DriverCompany driverCompany = driverCompanyRepository.findByDriverId(userId);

        boolean currentStatus = driverCompany.getVerified();
        driverCompany.setVerified(!currentStatus);

        User user = driverCompany.getUser();

        if (!currentStatus) {
            Role driverRole = roleRepository.findById(4L)
                    .orElseThrow(() -> new IllegalArgumentException("Role DRIVER not found"));
            user.setRole(driverRole);

            emailService.sendDriverApprovalEmail(user.getEmail(), user.getLastname(), user.getFirstname());
        } else {
            Role defaultRole = roleRepository.findById(1L)
                    .orElseThrow(() -> new IllegalArgumentException("Default role not found"));
            user.setRole(defaultRole);

            emailService.sendDriverSuspensionEmail(user.getEmail(), user.getLastname(), user.getFirstname());
        }

        userRepository.save(user);
        return driverCompanyRepository.save(driverCompany);
    }




    @Override
    public List<DriverDTO> getDriversByCompanyId(Long companyId) {
        List<DriverCompany> driverCompanies = driverCompanyRepository.findDriversByCompanyId(companyId);

        return driverCompanies.stream().map(driverCompany -> {
            User user = driverCompany.getUser();
            return DriverDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .isActive(user.getIsActive())
                    .avatar(user.getAvatar())
                    .isVerified(driverCompany.getVerified())
                    .build();
        }).collect(Collectors.toList());
    }
    @Override
    public List<DriverDTO> getVerifiedDriversByCompanyId(Long companyId) {
        List<DriverCompany> driverCompanies = driverCompanyRepository.findVerifiedDriversByCompanyId(companyId);

        return driverCompanies.stream().map(driverCompany -> {
            User user = driverCompany.getUser();
            return DriverDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .isActive(user.getIsActive())
                    .avatar(user.getAvatar())
                    .isVerified(driverCompany.getVerified())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<DriverDTO> getAvailableDriversByCompanyAndDate(Long companyId, Date date) {
        List<DriverCompany> availableDrivers = driverCompanyRepository.findAvailableDriversByCompanyAndDate(companyId, date);
        return availableDrivers.stream().map(driverCompany -> {
            User user = driverCompany.getUser();
            return DriverDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .isActive(user.getIsActive())
                    .avatar(user.getAvatar())
                    .isVerified(driverCompany.getVerified())
                    .build();
        }).collect(Collectors.toList());
    }

}
