package com.busstation.services.impl;

import com.busstation.dtos.TicketDTO;
import com.busstation.dtos.UserDTO;
import com.busstation.mappers.TicketDTOMapper;
import com.busstation.mappers.UserDTOMapper;
import com.busstation.pojo.Role;
import com.busstation.pojo.User;
import com.busstation.repositories.RoleRepository;
import com.busstation.repositories.UserRepository;
import com.busstation.services.CloudinaryService;
import com.busstation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDTOMapper userDTOMapper;

    @Autowired
    private TicketDTOMapper ticketDTOMapper;


   @Autowired
   private CloudinaryService cloudinaryService;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.getUserByUserName(s);
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.isEmailExist(email);
    }

    @Override
    public UserDTO toDTO(User user) {
        return userDTOMapper.apply(user);
    }

    @Override
    public UserDTO getAuthenticatedUser() {
       User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       return userDTOMapper.apply(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> listActiveUsers() {
        return userRepository.listActiveUsers()
                .stream()
                .map(userDTOMapper::apply)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findActiveUsersByRoleId(Long roleId) {
        return userRepository.findActiveUsersByRoleId(roleId)
                .stream()
                .map(userDTOMapper::apply)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        User user = userRepository.getUserById(id);
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional
    public void changeRole(Long userId, Long roleId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        Role role = roleRepository.findById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("Role not found");
        }
        user.setRole(role);
        userRepository.changeRole(user);
    }


    @Override
    public UserDTO updateUser(Long id, UserDTO payload, MultipartFile avatar) throws IllegalAccessException, IOException {
        User user = userRepository.getUserById(id);

        for (Field payloadField : payload.getClass().getDeclaredFields()) {
            payloadField.setAccessible(true);
           Object fieldData = payloadField.get(payload);
           String fieldName = payloadField.getName();
           // exclude fields
           if (fieldName.equals("username") && fieldName.equals("email")) {
               continue;
           }
           if ( fieldData != null) {
               for (Field userField : user.getClass().getDeclaredFields()) {
                   if (userField.getName().equals(fieldName)) {
                       userField.setAccessible(true);
                       userField.set(user, fieldData);
                       break;
                   }
               }
           }

        }
        if (avatar != null) {
            String url = cloudinaryService.uploadFile(avatar);
            user.setAvatar(url);
        }
        userRepository.update(user);
        return userDTOMapper.apply(user);
    }

    @Override
    public long getUserCountByRoleId(Long roleId) {
        return userRepository.countUsersByRoleId(roleId);
    }
}
