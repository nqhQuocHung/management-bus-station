package com.busstation.services.impl;

import com.busstation.dtos.*;
import com.busstation.mappers.RouteDTOMapper;
import com.busstation.mappers.TripDTOMapper;
import com.busstation.pojo.Route;
import com.busstation.pojo.Station;
import com.busstation.pojo.TransportationCompany;
import com.busstation.pojo.Trip;
import com.busstation.repositories.RouteRepository;
import com.busstation.repositories.StationRepository;
import com.busstation.repositories.TransportationCompanyRepository;
import com.busstation.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:configuration.properties")
@Transactional
public class RouteServiceImpl implements RouteService {

    @Autowired
    private Environment environment;

    @Autowired
    private RouteRepository repository;

    @Autowired
    private RouteDTOMapper routeDTOMapper;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private TransportationCompanyRepository transportationCompanyRepository;

    @Autowired
    private TripDTOMapper tripDTOMapper;
    @Override
    public Map<String, Object> list(Map<String, String> params) {
        List<Route> results = repository.list(params);
        Long total = repository.count(params);
        int pageSize = Integer.parseInt(environment.getProperty("route.pageSize"));
        int pageTotal = (int) Math.ceil(total / pageSize) ;
        Map<String, Object> m = new HashMap<>();
        m.put("results", results.stream().map(routeDTOMapper::apply).collect(Collectors.toList()));
        m.put("total", total);
        m.put("pageTotal", pageTotal);
        return m;
    }

    @Override
    public RouteDTO getById(Long id) {
        Route route = repository.getById(id);
        return routeDTOMapper.apply(route);
    }

    @Override
    public List<TripDTO> getTripList(Long id) {
        Route route = repository.getById(id);
        if (route == null)  throw new IllegalArgumentException("Invalid id");
        List<Trip> trips = (List<Trip>) route.getTrips();
        return trips.stream().map(tripDTOMapper::apply).collect(Collectors.toList());
    }

    @Transactional
    public void createRoute(RouteDetailDTO routeDetailDTO) {
        TransportationCompany company = transportationCompanyRepository.findById(routeDetailDTO.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Station fromStation = stationRepository.getStationById(routeDetailDTO.getFromStationId())
                .orElseThrow(() -> new IllegalArgumentException("From station not found"));

        Station toStation = stationRepository.getStationById(routeDetailDTO.getToStationId())
                .orElseThrow(() -> new IllegalArgumentException("To station not found"));

        Route route = Route.builder()
                .name(routeDetailDTO.getName())
                .company(company)
                .fromStation(fromStation)
                .toStation(toStation)
                .seatPrice(routeDetailDTO.getSeatPrice())
                .cargoPrice(routeDetailDTO.getCargoPrice())
                .isActive(routeDetailDTO.getIsActive())
                .build();

        repository.save(route);
    }

    @Override
    @Transactional
    public List<RoutePublicDTO> getRoutesByCompanyId(Long companyId) {
        List<Route> routes = repository.findByCompanyId(companyId);
        return routes.stream()
                .map(route -> RoutePublicDTO.builder()
                        .id(route.getId())
                        .name(route.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
