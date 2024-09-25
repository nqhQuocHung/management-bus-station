package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.RouteDTO;
import com.nqh.bus_station_management.bus_station.dtos.RouteRegisterDTO;
import com.nqh.bus_station_management.bus_station.mappers.RouteDTOMapper;
import com.nqh.bus_station_management.bus_station.pojo.Route;
import com.nqh.bus_station_management.bus_station.pojo.Station;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.repositories.CompanyRepository;
import com.nqh.bus_station_management.bus_station.repositories.RouteRepository;
import com.nqh.bus_station_management.bus_station.repositories.StationRepository;
import com.nqh.bus_station_management.bus_station.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nqh.bus_station_management.bus_station.dtos.RoutePublicDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    @Autowired
    private StationRepository stationRepository;


    @Autowired
    private CompanyRepository companyRepository;
    private final Environment environment;

    private final RouteDTOMapper routeDTOMapper;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository, Environment environment, RouteDTOMapper routeDTOMapper) {
        this.routeRepository = routeRepository;
        this.environment = environment;
        this.routeDTOMapper = routeDTOMapper;
    }

    @Override
    public Map<String, Object> listRoutes(Map<String, String> params) {
        String name = params.get("name");
        Boolean isActive = params.get("isActive") != null ? Boolean.parseBoolean(params.get("isActive")) : null; // Lọc theo trạng thái hoạt động nếu có

        int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0; // Chuyển page về dạng index cho Pageable (0-based)
        int pageSize = Integer.parseInt(environment.getProperty("route.pageSize", "10")); // Kích thước trang mặc định là 10
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Route> routesPage = routeRepository.list(name, isActive, pageable);

        List<Map<String, Object>> routeList = routesPage.getContent().stream().map(route -> {
            Map<String, Object> routeMap = new HashMap<>();
            routeMap.put("id", route.getId());
            routeMap.put("name", route.getName());
            routeMap.put("isActive", route.getIsActive());
            routeMap.put("createdAt", route.getCreatedAt());
            routeMap.put("seatPrice", route.getSeatPrice());
            routeMap.put("cargoPrice", route.getCargoPrice());

            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("id", route.getCompany().getId());
            companyMap.put("name", route.getCompany().getName());
            routeMap.put("company", companyMap);

            Map<String, Object> fromStationMap = new HashMap<>();
            fromStationMap.put("id", route.getFromStation().getId());
            fromStationMap.put("address", route.getFromStation().getAddress());
            fromStationMap.put("mapUrl", route.getFromStation().getMapUrl());
            routeMap.put("fromStation", fromStationMap);

            Map<String, Object> toStationMap = new HashMap<>();
            toStationMap.put("id", route.getToStation().getId());
            toStationMap.put("address", route.getToStation().getAddress());
            toStationMap.put("mapUrl", route.getToStation().getMapUrl());
            routeMap.put("toStation", toStationMap);

            return routeMap;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("results", routeList);
        response.put("total", routesPage.getTotalElements());
        response.put("pageTotal", routesPage.getTotalPages());

        return response;
    }

    @Override
    public Long countRoutes(Map<String, String> params) {
        String name = params.get("name");
        Boolean isActive = params.get("isActive") != null ? Boolean.parseBoolean(params.get("isActive")) : null;
        return routeRepository.count(name, isActive);
    }

    public RouteDTO getRouteById(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        return routeDTOMapper.apply(route);
    }

    @Override
    public Route createRoute(RouteRegisterDTO routeDTO) {
        TransportationCompany company = companyRepository.findById(routeDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Station fromStation = stationRepository.findById(routeDTO.getFromStationId())
                .orElseThrow(() -> new RuntimeException("From Station not found"));
        Station toStation = stationRepository.findById(routeDTO.getToStationId())
                .orElseThrow(() -> new RuntimeException("To Station not found"));

        Route newRoute = Route.builder()
                .name(routeDTO.getName())
                .isActive(routeDTO.getIsActive())
                .seatPrice(routeDTO.getSeatPrice())
                .cargoPrice(routeDTO.getCargoPrice())
                .company(company)
                .fromStation(fromStation)
                .toStation(toStation)
                .build();

        return routeRepository.save(newRoute);
    }

    @Override
    public void deleteRouteById(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public List<RoutePublicDTO> getRoutesByCompanyId(Long companyId) {
        List<Route> routes = routeRepository.findByCompanyId(companyId);
        return routes.stream().map(route -> RoutePublicDTO.builder()
                .id(route.getId())
                .name(route.getName())
                .build()).collect(Collectors.toList());
    }

    @Override
    public RouteDTO getById(Long id) {
        Route route = routeRepository.getById(id);
        if (route == null) {
            throw new IllegalArgumentException("Route id not found: " + id);
        }
        return routeDTOMapper.apply(route);
    }
}
