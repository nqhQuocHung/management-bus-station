package com.busstation.repositories;

import com.busstation.dtos.StatisticsDTO;
import com.busstation.pojo.OnlinePaymentResult;
import com.busstation.pojo.Ticket;

import java.util.List;
import java.util.Map;

public interface TicketRepository {
    void saveAll(List<Ticket> tickets);
    void updateAll(List<Ticket> tickets);
    Map<Integer, StatisticsDTO> calculateAnnualRevenue(int year, Long companyId);
    Map<Integer, StatisticsDTO> calculateQuarterlyRevenue(int year, Long companyId);
    Map<Integer, StatisticsDTO> calculateDailyRevenue(int year, int month, int day, Long companyId);
    List<Ticket> findTicketsByUserId(Long userId);
    void delete(Long id);

    Ticket getById(Long id);
}
