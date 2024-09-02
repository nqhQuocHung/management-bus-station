package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.UserProfileDTO;
import com.nqh.bus_station_management.bus_station.pojo.User;

public interface UserService {
    UserProfileDTO getUserById(Long id);
    UserProfileDTO getUserByUsername(String username);
    User saveUser(User user);
    void deleteUserById(Long id);
}
