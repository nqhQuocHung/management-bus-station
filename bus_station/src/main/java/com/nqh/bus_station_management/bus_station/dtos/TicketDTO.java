package com.nqh.bus_station_management.bus_station.dtos;


import com.nqh.bus_station_management.bus_station.pojo.PaymentMethod;
import com.nqh.bus_station_management.bus_station.pojo.Seat;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class TicketDTO {
    private Long ticketId;
    private RouteDTO routeInfo;

    private Seat seat;

    private Timestamp paidAt;
    private Double seatPrice;

    private Double cargoPrice;

    private TripDTO tripInfo;
    private PaymentMethod paymentMethod;

}
