package com.nqh.bus_station_management.bus_station.dtos;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CheckoutResponse {

    private String paymentUrl;
    private List<TicketDTO> tickets;
}
