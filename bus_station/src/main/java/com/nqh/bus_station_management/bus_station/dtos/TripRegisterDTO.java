package com.nqh.bus_station_management.bus_station.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRegisterDTO {
    private Long routeId;
    private Long carId;
    private Long driverId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp departAt;
    private Boolean isActive;
}
