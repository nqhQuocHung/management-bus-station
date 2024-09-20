package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.UserDTO;
import com.nqh.bus_station_management.bus_station.dtos.UserProfileDTO;
import com.nqh.bus_station_management.bus_station.dtos.UserRegisterDTO;
import com.nqh.bus_station_management.bus_station.dtos.UserUpdateDTO;
import com.nqh.bus_station_management.bus_station.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserProfileDTO getUserById(Long id);
    UserProfileDTO getUserByUsername(String username);
    User saveUser(UserRegisterDTO userRegisterDTO);
    boolean deleteUserById(Long id);
    User updateUser(Long id, UserUpdateDTO userUpdateDTO);
    List<UserDTO> getUsersByRole(Long roleId);
    UserDTO toDTO(User user);
    UserDetails loadUserByUsername(String username);
    boolean isEmailExist(String email);
}
