package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.*;
import com.nqh.bus_station_management.bus_station.mappers.UserDTOMapper;
import com.nqh.bus_station_management.bus_station.mappers.UserProfileDTOMapper;
import com.nqh.bus_station_management.bus_station.pojo.Role;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import com.nqh.bus_station_management.bus_station.services.EmailService;
import com.nqh.bus_station_management.bus_station.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDTOMapper userDTOMapper;

    @Autowired
    private UserProfileDTOMapper userProfileDTOMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public UserProfileDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return userProfileDTOMapper.toDTO(user);
    }

    @Override
    public User saveUser(UserRegisterDTO userRegisterDTO) {
        if (userRepository.existsByUsername(userRegisterDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        User user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(encodedPassword)
                .firstname(userRegisterDTO.getFirstName())
                .lastname(userRegisterDTO.getLastName())
                .email(userRegisterDTO.getEmail())
                .phone(userRegisterDTO.getPhone())
                .avatar(userRegisterDTO.getAvatar())
                .isActive(true)
                .role(new Role(1L, "USER"))
                .build();

        User savedUser = userRepository.save(user);

        emailService.sendRegistrationSuccessEmail(
                savedUser.getEmail(),
                savedUser.getFirstname(),
                savedUser.getLastname(),
                savedUser.getUsername(),
                userRegisterDTO.getPassword()
        );

        return savedUser;
    }

    @Override
    public boolean deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public UserProfileDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return userProfileDTOMapper.toDTO(user);
    }

    @Override
    public User updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            System.out.println("User not found with id " + id);
            return null;
        }
        User user = optionalUser.get();
        user.setFirstname(userUpdateDTO.getFirstName());
        user.setLastname(userUpdateDTO.getLastName());
        user.setPhone(userUpdateDTO.getPhone());
        user.setAvatar(userUpdateDTO.getAvatar());
        return userRepository.save(user);
    }
    @Override
    public List<UserDTO> getUsersByRole(Long roleId) {
        List<User> users = userRepository.findActiveUsersByRoleId(roleId);
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole().getName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                user.getAuthorities()
        );
    }

    @Override
    public UserDTO toDTO(User user) {
        return userDTOMapper.apply(user);
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<StatisticsUserDTO> getUserStatistics() {
        return userRepository.countUsersByRole();
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
