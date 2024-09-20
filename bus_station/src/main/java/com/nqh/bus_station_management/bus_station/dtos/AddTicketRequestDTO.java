package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTicketRequestDTO {
    private Long seatId;
    private Long tripId;
    private Long customerId;
    private Double seatPrice;
    private Timestamp paidAt;
}
