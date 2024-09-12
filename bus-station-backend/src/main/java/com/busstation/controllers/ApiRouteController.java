package com.busstation.controllers;

import com.busstation.dtos.RouteDTO;
import com.busstation.dtos.RouteDetailDTO;
import com.busstation.dtos.RoutePublicDTO;
import com.busstation.pojo.Route;
import com.busstation.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/route")
public class ApiRouteController {

    @Autowired
    private RouteService service;

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.list(params));
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Object> retrieve(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/trip")
    public ResponseEntity<Object> handleRouteDetails(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTripList(id));
    }

    @PostMapping("/add")
    public ResponseEntity<String> createRoute(@RequestBody RouteDetailDTO routeDetailDTO) {
        try {
            System.out.println("Received Route Detail: " + routeDetailDTO);
            service.createRoute(routeDetailDTO);
            return ResponseEntity.ok("Route created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create route");
        }
    }

    @GetMapping("/company/{companyId}")
    public List<RoutePublicDTO> getRoutesByCompanyId(@PathVariable Long companyId) {
        return service.getRoutesByCompanyId(companyId);
    }
}
