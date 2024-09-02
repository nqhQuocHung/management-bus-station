package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.UserProfileDTO;
import com.nqh.bus_station_management.bus_station.mappers.UserProfileDTOMapper;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import com.nqh.bus_station_management.bus_station.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserProfileDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return UserProfileDTOMapper.toDTO(user);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserProfileDTO getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserProfileDTOMapper::toDTO).orElse(null);
    }
}
