package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.*;
import com.nqh.bus_station_management.bus_station.pojo.User;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    AuthenticationResponse register(RegisterRequest registerRequest);

    AuthenticationResponse loginWithGoogle(LoginWithGoogleRequest data);

    void forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    void changePassword(User user, ChangePasswordRequestDTO changePasswordRequest);
}
