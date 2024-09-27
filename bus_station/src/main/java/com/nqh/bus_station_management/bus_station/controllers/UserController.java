package com.nqh.bus_station_management.bus_station.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nqh.bus_station_management.bus_station.dtos.UserDTO;
import com.nqh.bus_station_management.bus_station.dtos.UserProfileDTO;
import com.nqh.bus_station_management.bus_station.dtos.UserRegisterDTO;
import com.nqh.bus_station_management.bus_station.dtos.UserUpdateDTO;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.services.CloudinaryService;
import com.nqh.bus_station_management.bus_station.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileDTO> getUserByUsername(@PathVariable String username) {
        UserProfileDTO user = userService.getUserByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUserById(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
        UserProfileDTO user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        User updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UserDTO>> findActiveUsersByRoleId(@PathVariable Long roleId) {
        List<UserDTO> users = userService.getUsersByRole(roleId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @ModelAttribute UserRegisterDTO userRegisterDTO) {
        try {
            if (userRegisterDTO.getAvatar() == null || userRegisterDTO.getAvatar().isEmpty()) {
                userRegisterDTO.setAvatar("https://cellphones.com.vn/sforum/wp-content/uploads/2023/10/avatar-trang-4.jpg");
            }
            User savedUser = userService.saveUser(userRegisterDTO);

            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
