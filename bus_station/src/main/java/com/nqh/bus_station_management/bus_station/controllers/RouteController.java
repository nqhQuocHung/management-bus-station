package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.RoutePublicDTO;
import com.nqh.bus_station_management.bus_station.dtos.RouteRegisterDTO;
import com.nqh.bus_station_management.bus_station.pojo.Route;
import com.nqh.bus_station_management.bus_station.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public Map<String, Object> listRoutes(@RequestParam Map<String, String> params) {
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

    @PostMapping("/add")
    public ResponseEntity<Route> createRoute(@RequestBody RouteRegisterDTO routeDTO) {
        Route newRoute = routeService.createRoute(routeDTO);
        return ResponseEntity.ok(newRoute);
    }

    @DeleteMapping("/{id}")
    public void deleteRouteById(@PathVariable Long id) {
        routeService.deleteRouteById(id);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<RoutePublicDTO>> getRoutesByCompanyId(@PathVariable Long companyId) {
        List<RoutePublicDTO> routes = routeService.getRoutesByCompanyId(companyId);
        return ResponseEntity.ok(routes);
    }
}
