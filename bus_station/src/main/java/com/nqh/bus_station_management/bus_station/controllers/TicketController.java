package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public void saveAllTickets(@RequestBody List<Ticket> tickets) {
        ticketService.saveAllTickets(tickets);
    }

    @PostMapping("/update")
    public void updateAllTickets(@RequestBody List<Ticket> tickets) {
        ticketService.updateAllTickets(tickets);
    }

    @GetMapping("/revenue/annual")
    public Map<Integer, StatisticsDTO> calculateAnnualRevenue(@RequestParam int year, @RequestParam Long companyId) {
        return ticketService.calculateAnnualRevenue(year, companyId);
    }

    @GetMapping("/revenue/quarterly")
    public Map<Integer, StatisticsDTO> calculateQuarterlyRevenue(@RequestParam int year, @RequestParam Long companyId) {
        return ticketService.calculateQuarterlyRevenue(year, companyId);
    }

    @GetMapping("/revenue/daily")
    public Map<Integer, StatisticsDTO> calculateDailyRevenue(@RequestParam int year, @RequestParam int month, @RequestParam int day, @RequestParam Long companyId) {
        return ticketService.calculateDailyRevenue(year, month, day, companyId);
    }

    @GetMapping("/user/{userId}")
    public List<Ticket> findTicketsByUserId(@PathVariable Long userId) {
        return ticketService.findTicketsByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteTicketById(@PathVariable Long id) {
        ticketService.deleteTicketById(id);
    }

    @GetMapping("/{id}")
    public Optional<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }
}
