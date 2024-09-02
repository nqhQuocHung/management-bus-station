package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.pojo.Route;
import com.nqh.bus_station_management.bus_station.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public List<Route> listRoutes(@RequestParam Map<String, String> params) {
        return routeService.listRoutes(params);
    }

    @GetMapping("/count")
    public Long countRoutes(@RequestParam Map<String, String> params) {
        return routeService.countRoutes(params);
    }

    @GetMapping("/{id}")
    public Optional<Route> getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    @PostMapping
    public Route saveRoute(@RequestBody Route route) {
        return routeService.saveRoute(route);
    }

    @DeleteMapping("/{id}")
    public void deleteRouteById(@PathVariable Long id) {
        routeService.deleteRouteById(id);
    }

    @GetMapping("/company/{companyId}")
    public List<Route> findRoutesByCompanyId(@PathVariable Long companyId) {
        return routeService.findRoutesByCompanyId(companyId);
    }
}
