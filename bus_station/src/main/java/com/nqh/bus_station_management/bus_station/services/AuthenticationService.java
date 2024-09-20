package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.AuthenticationRequest;
import com.nqh.bus_station_management.bus_station.dtos.AuthenticationResponse;
import com.nqh.bus_station_management.bus_station.dtos.LoginWithGoogleRequest;
import com.nqh.bus_station_management.bus_station.dtos.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    AuthenticationResponse register(RegisterRequest registerRequest);

    AuthenticationResponse loginWithGoogle(LoginWithGoogleRequest data);
}
