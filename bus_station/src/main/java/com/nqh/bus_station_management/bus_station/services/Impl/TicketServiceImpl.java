package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.*;
import com.nqh.bus_station_management.bus_station.pojo.*;
import com.nqh.bus_station_management.bus_station.repositories.*;
import com.nqh.bus_station_management.bus_station.services.RouteService;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import com.nqh.bus_station_management.bus_station.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CargoRepository cargoRepository;

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
    @Transactional
    public void deleteTicketById(Long id) {
        ticketRepository.deleteCargosByTicketId(id);
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

    @Override
    public Ticket addTicket(AddTicketRequestDTO requestDTO) {
        Seat seat = seatRepository.findById(requestDTO.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        Trip trip = tripRepository.findById(requestDTO.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        User customer = userRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Ticket ticket = Ticket.builder()
                .seat(seat)
                .trip(trip)
                .customer(customer)
                .seatPrice(requestDTO.getSeatPrice())
                .paidAt(requestDTO.getPaidAt())
                .build();

        return ticketRepository.save(ticket);
    }

    @Override
    public List<TicketDetailDTO> getTicketDetails(List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found");
        }
        return tickets.stream()
                .map(this::convertToTicketDetailDTO)
                .collect(Collectors.toList());
    }
    private TicketDetailDTO convertToTicketDetailDTO(Ticket ticket) {
        return TicketDetailDTO.builder()
                .ticketId(ticket.getId())
                .seatCode(ticket.getSeat() != null && ticket.getSeat().getSeatCode() != null ? ticket.getSeat().getSeatCode().getCode() : null)
                .routeName(ticket.getTrip() != null && ticket.getTrip().getRoute() != null ? ticket.getTrip().getRoute().getName() : null)
                .companyName(ticket.getTrip() != null && ticket.getTrip().getRoute() != null && ticket.getTrip().getRoute().getCompany() != null ? ticket.getTrip().getRoute().getCompany().getName() : null)
                .fromStation(ticket.getTrip() != null && ticket.getTrip().getRoute() != null && ticket.getTrip().getRoute().getFromStation() != null ? ticket.getTrip().getRoute().getFromStation().getAddress() : null)
                .toStation(ticket.getTrip() != null && ticket.getTrip().getRoute() != null && ticket.getTrip().getRoute().getToStation() != null ? ticket.getTrip().getRoute().getToStation().getAddress() : null)
                .departAt(ticket.getTrip() != null ? ticket.getTrip().getDepartAt() : null)
                .seatPrice(ticket.getSeatPrice())
                .cargoPrice(ticket.getCargo() != null ? ticket.getCargo().getCargoPrice() : 0.0)
                .build();
    }

    @Override
    @Transactional
    public void deleteTicketAndCargoById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket với ID " + ticketId + " không tồn tại."));
        Cargo cargo = cargoRepository.findByTicket(ticket);
        if (cargo != null) {
            cargoRepository.delete(cargo);
        }
        ticketRepository.delete(ticket);
    }

}
