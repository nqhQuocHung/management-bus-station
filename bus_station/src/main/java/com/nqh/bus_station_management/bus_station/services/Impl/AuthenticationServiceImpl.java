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

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequest) {
        User user = userRepository.findByUsernameAndEmail(
                forgotPasswordRequest.getUsername(),
                forgotPasswordRequest.getEmail()
        ).orElseThrow(() -> new IllegalArgumentException("User not found with the provided username and email"));

        String newPassword = user.getUsername();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        String subject = "Yêu cầu đặt lại mật khẩu tại Bus Station";
        String message = String.format(
                "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                        + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                        + "<h2 style=\"color: #007bff; text-align: center;\">Đặt lại mật khẩu</h2>"
                        + "<p>Xin chào %s,</p>"
                        + "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>"
                        + "<p>Mật khẩu tạm thời mới của bạn là: <strong>%s</strong></p>"
                        + "<p>Vui lòng đăng nhập với mật khẩu tạm thời này và thay đổi mật khẩu mới ngay lập tức để bảo vệ tài khoản của bạn.</p>"
                        + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng liên hệ với chúng tôi ngay lập tức.</p>"
                        + "<p style=\"text-align: right;\">Trân trọng,<br>Đội ngũ hỗ trợ Bus Station</p>"
                        + "</div>"
                        + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                        + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                        + "</div></body></html>",
                user.getLastname() + " " + user.getFirstname(),
                newPassword
        );
        emailService.sendHtmlEmail(user.getEmail(), subject, message);
    }

    @Override
    public void changePassword(User user, ChangePasswordRequestDTO changePasswordRequest) {
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        String subject = "Thông báo: Đổi mật khẩu thành công";
        String message = String.format(
                "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                        + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                        + "<h2 style=\"color: #28a745; text-align: center;\">Đổi mật khẩu thành công</h2>"
                        + "<p>Xin chào %s %s,</p>"
                        + "<p>Bạn đã thay đổi mật khẩu thành công. Nếu bạn không yêu cầu thay đổi mật khẩu, vui lòng liên hệ với bộ phận hỗ trợ của chúng tôi ngay lập tức.</p>"
                        + "<p style=\"text-align: right;\">Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                        + "</div>"
                        + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                        + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                        + "</div></body></html>",
                user.getLastname(), user.getFirstname()
        );
        emailService.sendHtmlEmail(user.getEmail(), subject, message);
    }

    @Override
    public void sendOtp(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username không tồn tại trong hệ thống"));
        String email = user.getEmail();
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setOtp(otp);
        user.setOtpCreationTime(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        String subject = "Mã OTP đăng nhập của bạn";
        String message = String.format(
                "<html><body style=\"font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; color: #333;\">"
                        + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                        + "<h2 style=\"text-align: center; color: #007bff;\">Mã OTP đăng nhập</h2>"
                        + "<p style=\"font-size: 16px;\">Xin chào,</p>"
                        + "<p style=\"font-size: 16px;\">Để đăng nhập vào tài khoản của bạn, vui lòng sử dụng mã OTP sau:</p>"
                        + "<h3 style=\"text-align: center; font-size: 24px; color: #28a745;\">%s</h3>"
                        + "<p style=\"font-size: 14px; color: #555;\">Mã này sẽ hết hạn sau 5 phút.</p>"
                        + "<p style=\"font-size: 14px;\">Trân trọng,</p>"
                        + "<p style=\"font-size: 14px; color: #007bff;\">Đội ngũ hỗ trợ của chúng tôi</p>"
                        + "</div>"
                        + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                        + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                        + "</div></body></html>",
                otp
        );

        emailService.sendHtmlEmail(email, subject, message);
    }


    @Override
    public AuthenticationResponse authenticateWithOtp(String username, String otp) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Username không tồn tại trong hệ thống"));
        if (user.getOtp() == null || !user.getOtp().equals(otp) || System.currentTimeMillis() - user.getOtpCreationTime().getTime() > 5 * 60 * 1000) {
            throw new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn.");
        }
        user.setOtp(null);
        user.setOtpCreationTime(null);
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder().accessToken(token).userDetails(userDetailsService.toDTO(user)).build();
    }
}
