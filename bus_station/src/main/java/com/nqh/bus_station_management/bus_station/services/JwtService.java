package com.nqh.bus_station_management.bus_station.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface JwtService {

    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, Map<String, Object> extraClaims);

    boolean isTokenValid(String token, UserDetails userDetails);
    List<String> extractRoles(String token);
}
