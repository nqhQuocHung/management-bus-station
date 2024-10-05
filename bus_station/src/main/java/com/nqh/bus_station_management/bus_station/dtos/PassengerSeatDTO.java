package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerSeatDTO {

    private Long passengerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String seatCode;
}
