package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.DriverDTO;
import com.nqh.bus_station_management.bus_station.pojo.DriverCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;

import java.util.Date;
import java.util.List;

public interface DriverCompanyService {
    DriverCompany createDriverCompany(User user, TransportationCompany company);
    DriverCompany updateVerifiedStatus(Long driverCompanyId);
    List<DriverDTO> getDriversByCompanyId(Long companyId);
    List<DriverDTO> getVerifiedDriversByCompanyId(Long companyId);
    List<DriverDTO> getAvailableDriversByCompanyAndDate(Long companyId, Date date);
}
