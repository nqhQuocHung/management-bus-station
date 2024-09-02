package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.UserProfileDTO;
import com.nqh.bus_station_management.bus_station.pojo.Role;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/username/{username}")
    public UserProfileDTO getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public UserProfileDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
