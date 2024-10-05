package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.*;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;
import com.nqh.bus_station_management.bus_station.pojo.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TicketService {
    List<TicketDTO> getInfoFromCart(List<Map<String, String>> cart);
    void saveAllTickets(List<Ticket> tickets);
    void updateAllTickets(List<Ticket> tickets);
    List<StatisticsDTO> calculateAnnualRevenue(int year, Long companyId);
    List<StatisticsDTO> calculateQuarterlyRevenue(int year, Long companyId);
    List<StatisticsDTO> calculateDailyRevenue(int year, int month, int day, Long companyId);
    List<Ticket> findTicketsByUserId(Long userId);
    void deleteTicketById(Long id);
    Optional<Ticket> getTicketById(Long id);
    Ticket addTicket(AddTicketRequestDTO requestDTO);
    List<TicketDetailDTO> getTicketDetails(List<Long> ticketIds);
    void deleteTicketAndCargoById(Long ticketId);
    List<Ticket> updatePaymentIdForTickets(List<Long> ticketIds, Long paymentResultId, Long paymentMethodId) throws Exception;
    List<TicketDetailDTO> findPaidTicketsByUserId(Long userId);
    List<StatisticsAdminDTO> calculateMonthlyRevenueAdmin(int year);
}
