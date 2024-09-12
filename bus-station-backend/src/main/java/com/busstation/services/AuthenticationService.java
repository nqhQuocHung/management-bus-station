package com.busstation.services;

import com.busstation.dtos.AuthenticationRequest;
import com.busstation.dtos.AuthenticationResponse;
import com.busstation.dtos.LoginWithGoogleRequest;
import com.busstation.dtos.RegisterRequest;


public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    AuthenticationResponse register(RegisterRequest registerRequest);

    AuthenticationResponse loginWithGoogle(LoginWithGoogleRequest data);
}
