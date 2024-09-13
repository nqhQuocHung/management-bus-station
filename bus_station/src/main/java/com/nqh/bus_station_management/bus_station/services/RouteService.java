package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.RouteDTO;
import com.nqh.bus_station_management.bus_station.pojo.Route;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RouteService {
    Map<String, Object> listRoutes(Map<String, String> params);
    Long countRoutes(Map<String, String> params);
    Optional<Route> getRouteById(Long id);
    Route saveRoute(Route route);
    void deleteRouteById(Long id);
    List<Route> findRoutesByCompanyId(Long companyId);
    RouteDTO getById(Long id);
}
