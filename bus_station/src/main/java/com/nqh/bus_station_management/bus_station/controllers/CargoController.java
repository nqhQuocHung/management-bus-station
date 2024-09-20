package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.CargoDTO;
import com.nqh.bus_station_management.bus_station.pojo.Cargo;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;
import com.nqh.bus_station_management.bus_station.repositories.TicketRepository;
import com.nqh.bus_station_management.bus_station.services.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping
    public ResponseEntity<Cargo> createCargo(@Validated @RequestBody CargoDTO cargoDTO) {
        List<Ticket> tickets = ticketRepository.findAllById(Collections.singleton(cargoDTO.getTicketId()));
        if (tickets.isEmpty()) {
            throw new RuntimeException("Ticket not found");
        }
        Ticket ticket = tickets.get(0);

        Cargo cargo = new Cargo();
        cargo.setReceiverName(cargoDTO.getReceiverName());
        cargo.setReceiverEmail(cargoDTO.getReceiverEmail());
        cargo.setReceiverPhone(cargoDTO.getReceiverPhone());
        cargo.setReceiverAddress(cargoDTO.getReceiverAddress());
        cargo.setCargoPrice(cargoDTO.getCargoPrice());
        cargo.setDescription(cargoDTO.getDescription());
        cargo.setTicket(ticket);
        Cargo createdCargo = cargoService.createCargo(cargo);
        return ResponseEntity.ok(createdCargo);
    }

}
