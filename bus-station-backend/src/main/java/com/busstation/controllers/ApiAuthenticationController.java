package com.busstation.controllers;

import com.busstation.dtos.AuthenticationRequest;
import com.busstation.dtos.LoginWithGoogleRequest;
import com.busstation.dtos.RegisterRequest;
import com.busstation.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")

public class ApiAuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {

        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));

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
