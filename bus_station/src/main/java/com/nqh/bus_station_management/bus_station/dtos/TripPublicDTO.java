package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPublicDTO {
    private Long id;
    private String routeName;
    private String carNumber;
    private Timestamp departAt;
    private Boolean status;
}
