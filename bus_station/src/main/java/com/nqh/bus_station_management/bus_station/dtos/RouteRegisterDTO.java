package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRegisterDTO {

    private String name;
    private Boolean isActive;
    private Double seatPrice;
    private Double cargoPrice;
    private Long companyId;
    private Long fromStationId;
    private Long toStationId;
}
