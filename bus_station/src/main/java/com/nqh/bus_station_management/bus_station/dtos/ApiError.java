package com.nqh.bus_station_management.bus_station.dtos;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Data
public class ApiError {
    private HttpStatus status;
    private List<String> errors;

    public ApiError(HttpStatus status,  List<String> errors) {
        super();
        this.status = status;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String error) {
        super();
        this.status = status;
        errors = Arrays.asList(error);
    }
}
