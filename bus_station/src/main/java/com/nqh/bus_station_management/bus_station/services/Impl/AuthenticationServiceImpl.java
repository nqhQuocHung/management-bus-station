package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.*;
import com.nqh.bus_station_management.bus_station.pojo.Role;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.repositories.RoleRepository;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import com.nqh.bus_station_management.bus_station.services.AuthenticationService;
import com.nqh.bus_station_management.bus_station.services.EmailService;
import com.nqh.bus_station_management.bus_station.services.JwtService;
import com.nqh.bus_station_management.bus_station.services.UserService;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;


import java.sql.Timestamp;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            User userDetails = userRepository.findByUsername(authenticationRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtService.generateToken(userDetails);
            return AuthenticationResponse.builder()
                    .accessToken(token)
                    .userDetails(userDetailsService.toDTO(userDetails))
                    .build();

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }




    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        User user = (User) userDetailsService.loadUserByUsername(registerRequest.getUsername());
        if (user != null) {
            throw new IllegalArgumentException("User name  is exist");
        } else {
            if (userDetailsService.isEmailExist(registerRequest.getEmail())) {
                throw new IllegalArgumentException("Email is exist");
            }
        }
        Role role = roleRepository.getRoleByName(registerRequest.getRole());
        if (role == null) {
            throw new IllegalArgumentException("Role field is invalid");
        }
        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .isActive(true)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        userRepository.save(newUser);
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(newUser))
                .userDetails(userDetailsService.toDTO(newUser))
                .build();
    }

    @Override
    public AuthenticationResponse loginWithGoogle(LoginWithGoogleRequest data) {
        User user = userRepository.getUserByEmail(data.getEmail());
        if (user == null) {
            Role role = roleRepository.getRoleByName("USER");
            String password = passwordEncoder.encode(generatePassword());
            user = User.builder()
                    .username(data.getUsername())
                    .email(data.getEmail())
                    .firstname(data.getFirstName())
                    .username(data.getEmail())
                    .lastname(data.getLastName())
                    .avatar(data.getAvatar())
                    .role(role)
                    .isActive(true)
                    .password(password)
                    .build();
            userRepository.save(user);
            emailService.sendEmail(
                    user.getEmail(),
                    "Đăng ký tài khoản tại Bus Station",
                    String.format("Mật khẩu tài khoản của bạn là %s", password));
        }

        String accessToken = jwtService.generateToken(user);
        UserDTO userDTO = userDetailsService.toDTO(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .userDetails(userDTO)
                .build();
    }

    private String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "-1";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        String password = gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }
}
