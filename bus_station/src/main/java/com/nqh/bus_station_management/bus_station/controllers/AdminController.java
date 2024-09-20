package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.UserDTO;
import com.nqh.bus_station_management.bus_station.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService  userService;

    @GetMapping("/users/role/{roleId}")
    public ResponseEntity<List<UserDTO>> findActiveUsersByRoleId(@PathVariable Long roleId) {
        List<UserDTO> users = userService.getUsersByRole(roleId);
        return ResponseEntity.ok(users);
    }
}
