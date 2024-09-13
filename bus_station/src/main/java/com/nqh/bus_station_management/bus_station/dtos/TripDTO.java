package com.nqh.bus_station_management.bus_station.dtos;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;


@Builder
@Data
public class TripDTO {
    private Long id;
    private Timestamp departAt;
}
