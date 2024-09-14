package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.AuthenticationRequest;
import com.nqh.bus_station_management.bus_station.dtos.AuthenticationResponse;
import com.nqh.bus_station_management.bus_station.dtos.LoginWithGoogleRequest;
import com.nqh.bus_station_management.bus_station.dtos.RegisterRequest;
import com.nqh.bus_station_management.bus_station.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.status(201).body(authenticationService.register(registerRequest));
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<Object> loginWithGoogle(@RequestBody LoginWithGoogleRequest data) {
        return ResponseEntity.ok(authenticationService.loginWithGoogle(data));
    }
}
