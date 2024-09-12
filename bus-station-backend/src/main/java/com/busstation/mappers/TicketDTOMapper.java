package com.busstation.mappers;

import com.busstation.dtos.RouteDTO;
import com.busstation.dtos.TicketDTO;
import com.busstation.dtos.TripDTO;
import com.busstation.pojo.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TicketDTOMapper implements Function<Ticket, TicketDTO> {

    @Autowired
    private RouteDTOMapper routeDTOMapper;

    @Autowired
    private TripDTOMapper tripDTOMapper;

    @Override
    public TicketDTO apply(Ticket ticket) {
        RouteDTO routeDTO = routeDTOMapper.apply(ticket.getTrip().getRoute());
        TripDTO tripDTO = tripDTOMapper.apply(ticket.getTrip());
       var result = TicketDTO.builder()
                .ticketId(ticket.getId())
                .routeInfo(routeDTO)
                .tripInfo(tripDTO)
                .seatPrice(ticket.getSeatPrice())

                .paidAt(ticket.getPaidAt())
                .paymentMethod(ticket.getPaymentMethod())
                .seat(ticket.getSeat());
       if (ticket.getCargo() != null) {
           result.cargoPrice(ticket.getCargo().getCargoPrice());
       }
       return result.build();
    }
}
