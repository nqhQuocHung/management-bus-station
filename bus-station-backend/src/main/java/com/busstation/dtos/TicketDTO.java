package com.busstation.dtos;

import com.busstation.pojo.PaymentMethod;
import com.busstation.pojo.Seat;
import com.busstation.pojo.Trip;
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
