package com.nqh.bus_station_management.bus_station.mappers;

import com.nqh.bus_station_management.bus_station.dtos.CompanyPublicDTO;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CompanyPublicMapper implements Function<TransportationCompany, CompanyPublicDTO> {
    @Override
    public CompanyPublicDTO apply(TransportationCompany transportationCompany) {
        return CompanyPublicDTO.builder()
                .id(transportationCompany.getId())
                .name(transportationCompany.getName())
                .build();
    }
}