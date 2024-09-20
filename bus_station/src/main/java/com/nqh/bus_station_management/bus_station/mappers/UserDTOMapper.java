package com.nqh.bus_station_management.bus_station.mappers;

import com.nqh.bus_station_management.bus_station.dtos.UserDTO;
import com.nqh.bus_station_management.bus_station.pojo.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service("userDTOMapper")
public class UserDTOMapper implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRole().getName())
                .build();
    }
}
