package com.nqh.bus_station_management.bus_station.dtos;

import lombok.Data;

@Data
public class TripRequestDTO {
    private String departAt;
    private Boolean isActive;
    private Long carId;
    private Long routeId;
}
