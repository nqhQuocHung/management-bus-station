package com.busstation.dtos;

import com.busstation.pojo.Ticket;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CheckoutResponse {

    private String paymentUrl;
    private List<TicketDTO> tickets;
}
