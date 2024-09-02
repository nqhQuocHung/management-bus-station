package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.pojo.Route;
import com.nqh.bus_station_management.bus_station.repositories.RouteRepository;
import com.nqh.bus_station_management.bus_station.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public List<Route> listRoutes(Map<String, String> params) {
        String name = params.get("name");
        Boolean isActive = params.get("isActive") != null ? Boolean.parseBoolean(params.get("isActive")) : null;
        return routeRepository.list(name, isActive);
    }

    @Override
    public Long countRoutes(Map<String, String> params) {
        String name = params.get("name");
        Boolean isActive = params.get("isActive") != null ? Boolean.parseBoolean(params.get("isActive")) : null;
        return routeRepository.count(name, isActive);
    }

    @Override
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }

    @Override
    public void deleteRouteById(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public List<Route> findRoutesByCompanyId(Long companyId) {
        return routeRepository.findByCompanyId(companyId);
    }
}
