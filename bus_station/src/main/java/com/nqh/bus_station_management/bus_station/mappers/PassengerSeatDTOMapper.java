package com.nqh.bus_station_management.bus_station.mappers;

import com.nqh.bus_station_management.bus_station.dtos.PassengerSeatDTO;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;

public class PassengerSeatDTOMapper {

    public static PassengerSeatDTO mapToPassengerSeatDTO(Ticket ticket) {
        return PassengerSeatDTO.builder()
                .passengerId(ticket.getCustomer().getId())
                .firstName(ticket.getCustomer().getFirstname())
                .lastName(ticket.getCustomer().getLastname())
                .email(ticket.getCustomer().getEmail())
                .phone(ticket.getCustomer().getPhone())
                .seatCode(ticket.getSeat() != null ? String.valueOf(ticket.getSeat().getSeatCode()) : null)
                .build();
    }
}

