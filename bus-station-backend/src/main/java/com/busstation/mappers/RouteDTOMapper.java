package com.busstation.mappers;

import com.busstation.dtos.RouteDTO;
import com.busstation.pojo.Route;
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
