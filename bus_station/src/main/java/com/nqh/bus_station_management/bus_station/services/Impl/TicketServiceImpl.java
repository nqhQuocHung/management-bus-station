package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;
import com.nqh.bus_station_management.bus_station.repositories.TicketRepository;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void saveAllTickets(List<Ticket> tickets) {
        ticketRepository.saveAll(tickets);
    }

    @Override
    public void updateAllTickets(List<Ticket> tickets) {
        ticketRepository.saveAll(tickets);
    }

    @Override
    public List<StatisticsDTO> calculateAnnualRevenue(int year, Long companyId) {
        return ticketRepository.calculateAnnualRevenue(year, companyId);
    }

    @Override
    public List<StatisticsDTO> calculateQuarterlyRevenue(int year, Long companyId) {
        return ticketRepository.calculateQuarterlyRevenue(year, companyId);
    }

    @Override
    public List<StatisticsDTO> calculateDailyRevenue(int year, int month, int day, Long companyId) {
        return ticketRepository.calculateDailyRevenue(year, month, day, companyId);
    }

    @Override
    public List<Ticket> findTicketsByUserId(Long userId) {
        return ticketRepository.findTicketsByUserId(userId);
    }

    @Override
    public void deleteTicketById(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }
}
