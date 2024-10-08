package com.nqh.bus_station_management.bus_station.mappers;

import com.nqh.bus_station_management.bus_station.dtos.RouteDTO;
import com.nqh.bus_station_management.bus_station.pojo.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RouteDTOMapper implements Function<Route, RouteDTO> {

    @Autowired
    private CompanyPublicMapper companyPublicMapper;
    @Override
    public RouteDTO apply(Route route) {
        return RouteDTO.builder()
                .id(route.getId())
                .name(route.getName())
                .fromStation(route.getFromStation())
                .toStation(route.getToStation())
                .seatPrice(route.getSeatPrice())
                .cargoPrice(route.getCargoPrice())
                .company(companyPublicMapper.apply(route.getCompany()))
                .build();
    }
}
