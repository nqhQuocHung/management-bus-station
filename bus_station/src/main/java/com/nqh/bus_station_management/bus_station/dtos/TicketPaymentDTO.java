package com.nqh.bus_station_management.bus_station.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPaymentDTO {
    private List<Long> ticketIds;
    private Long paymentId;
    private Long paymentMethodId;
}
