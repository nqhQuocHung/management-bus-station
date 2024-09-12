package com.busstation.services.impl;

import com.busstation.dtos.*;
import com.busstation.pojo.Role;
import com.busstation.pojo.User;
import com.busstation.repositories.RoleRepository;
import com.busstation.repositories.UserRepository;
import com.busstation.services.AuthenticationService;
import com.busstation.services.EmailService;
import com.busstation.services.JwtService;
import com.busstation.services.UserService;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        User userDetails = (User) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(userDetails))
                .userDetails(userDetailsService.toDTO(userDetails))
                .build();
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
        if (user.getEmail().equals(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is exist");
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
        userRepository.saveUser(newUser);
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
            userRepository.saveUser(user);
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
