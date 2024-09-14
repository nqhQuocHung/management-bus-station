package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.RouteDTO;
import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.dtos.TicketDTO;
import com.nqh.bus_station_management.bus_station.dtos.TripDTO;
import com.nqh.bus_station_management.bus_station.pojo.Seat;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;
import com.nqh.bus_station_management.bus_station.repositories.SeatRepository;
import com.nqh.bus_station_management.bus_station.repositories.TicketRepository;
import com.nqh.bus_station_management.bus_station.services.RouteService;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import com.nqh.bus_station_management.bus_station.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private TripService tripService;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
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

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }


    @Override
    public List<TicketDTO> getInfoFromCart(List<Map<String, String>> cart) {
        List<TicketDTO> results = new ArrayList<>();

        cart.forEach(c -> {
            try {
                Long routeId = Long.parseLong(c.get("routeId"));
                Long tripId = Long.parseLong(c.get("tripId"));
                Long seatId = Long.parseLong(c.get("seatId"));
                Boolean withCargo = Boolean.valueOf(c.get("withCargo"));

                RouteDTO routeInfo = routeService.getById(routeId);
                if (!withCargo) {
                    routeInfo.setCargoPrice(0.0);
                }
                TripDTO tripInfo = tripService.tripInfo(tripId);
                Seat seat = seatRepository.getById(seatId);

                TicketDTO newTicket = TicketDTO.builder()
                        .routeInfo(routeInfo)
                        .tripInfo(tripInfo)
                        .seat(seat)
                        .build();

                results.add(newTicket);

            } catch (NullPointerException e) {
                System.out.println("Lỗi khi xử lý mục trong giỏ hàng: " + e.getMessage());
            }
        });

        return results;
    }
}
