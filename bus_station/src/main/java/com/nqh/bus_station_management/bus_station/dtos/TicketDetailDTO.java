package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDetailDTO {
    private Long ticketId;
    private String seatCode;
    private String routeName;
    private String companyName;
    private String fromStation;
    private String toStation;
    private Timestamp departAt;
    private Double seatPrice;
    private Double cargoPrice;
    private Timestamp paidAt;
    private String paymentMethod;
}
