package com.busstation.mappers;

import com.busstation.dtos.UserDTO;
import com.busstation.pojo.User;
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
