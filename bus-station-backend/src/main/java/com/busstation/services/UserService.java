package com.busstation.services;

import com.busstation.dtos.TicketDTO;
import com.busstation.dtos.UserDTO;
import com.busstation.pojo.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface UserService extends UserDetailsService {
    boolean isEmailExist(String email);
    UserDTO toDTO(User user);
    UserDTO getAuthenticatedUser();
    List<UserDTO> listActiveUsers();
    List<UserDTO> findActiveUsersByRoleId(Long roleId);
    Optional<User> getUserById(Long id);
    void changeRole(Long userId, Long roleId);
    UserDTO updateUser(Long id, UserDTO payload, MultipartFile file) throws IllegalAccessException, IOException;
    long getUserCountByRoleId(Long roleId);
}
