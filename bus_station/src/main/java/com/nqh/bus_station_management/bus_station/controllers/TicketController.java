package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.AddTicketRequestDTO;
import com.nqh.bus_station_management.bus_station.dtos.TicketDTO;
import com.nqh.bus_station_management.bus_station.dtos.TicketDetailDTO;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> saveAllTickets(@RequestBody List<Ticket> tickets) {
        ticketService.saveAllTickets(tickets);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateAllTickets(@RequestBody List<Ticket> tickets) {
        ticketService.updateAllTickets(tickets);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> findTicketsByUserId(@PathVariable Long userId) {
        List<Ticket> tickets = ticketService.findTicketsByUserId(userId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable Long id) {
        ticketService.deleteTicketById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        return ticket.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/cart/details")
    public ResponseEntity<List<TicketDTO>> handleGetCartInfo(@RequestBody List<Map<String, String>> cart) {
        List<TicketDTO> ticketDetails = ticketService.getInfoFromCart(cart);
        return new ResponseEntity<>(ticketDetails, HttpStatus.OK);
    }

    @PostMapping("/add-cart")
    public ResponseEntity<List<Ticket>> addTicketsToCart(@RequestBody List<AddTicketRequestDTO> requestDTOs) {
        List<Ticket> tickets = new ArrayList<>();
        try {
            for (AddTicketRequestDTO requestDTO : requestDTOs) {
                Ticket ticket = ticketService.addTicket(requestDTO);
                tickets.add(ticket);
            }
            return ResponseEntity.ok(tickets);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> getTicketDetails(@RequestBody List<Long> ticketIds) {
        try {
            List<TicketDetailDTO> ticketDetails = ticketService.getTicketDetails(ticketIds);
            return ResponseEntity.ok(ticketDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No tickets found for the given IDs.");
        }
    }

    @DeleteMapping("/ticketAndCargo/{id}")
    public ResponseEntity<?> deleteTicketAndCargo(@PathVariable Long id) {
        try {
            ticketService.deleteTicketAndCargoById(id);
            return ResponseEntity.ok("Ticket và Cargo liên quan đã được xóa thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
