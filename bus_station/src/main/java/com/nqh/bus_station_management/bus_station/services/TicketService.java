package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    void saveAllTickets(List<Ticket> tickets);
    void updateAllTickets(List<Ticket> tickets);
    List<StatisticsDTO> calculateAnnualRevenue(int year, Long companyId);
    List<StatisticsDTO> calculateQuarterlyRevenue(int year, Long companyId);
    List<StatisticsDTO> calculateDailyRevenue(int year, int month, int day, Long companyId);
    List<Ticket> findTicketsByUserId(Long userId);
    void deleteTicketById(Long id);
    Optional<Ticket> getTicketById(Long id);
}
