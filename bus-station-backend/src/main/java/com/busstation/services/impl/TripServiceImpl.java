package com.busstation.services.impl;

import com.busstation.dtos.TripDTO;
import com.busstation.dtos.TripRequestDTO;
import com.busstation.mappers.TripDTOMapper;
import com.busstation.pojo.Car;
import com.busstation.pojo.Route;
import com.busstation.pojo.Seat;
import com.busstation.pojo.Trip;
import com.busstation.repositories.CarRepository;
import com.busstation.repositories.RouteRepository;
import com.busstation.repositories.TripRepository;
import com.busstation.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripDTOMapper tripDTOMapper;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RouteRepository routeRepository;


    @Override
    public Map<String, Object> seatDetails(Long id) {
        List<Seat> availableSeat = tripRepository.getAvailableSeats(id);
        List<Seat> unAvailableSeat = tripRepository.getUnAvailableSeats(id);
        Map<String, Object> result = new HashMap<>();
        result.put("availableSeat", availableSeat);
        result.put("unAvailableSeat", unAvailableSeat);
        return result;
    }

    @Override
    public TripDTO tripInfo(Long id) {
        Trip trip = tripRepository.getById(id);
        return tripDTOMapper.apply(trip);
    }

    @Override
    public Trip getById(Long id) {
        return tripRepository.getById(id);
    }

    @Override
    public Seat availableSeat(Long tripId, Long seatId) {
        return tripRepository.availableSeat(tripId, seatId);
    }


    @Transactional
    public void createTrip(TripRequestDTO tripRequestDTO) {
        Car car = carRepository.findById(tripRequestDTO.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        Route route = routeRepository.findById(tripRequestDTO.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp departTimestamp;
        try {
            departTimestamp = new Timestamp(dateFormat.parse(tripRequestDTO.getDepartAt()).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }

        Trip trip = Trip.builder()
                .departAt(departTimestamp)
                .car(car)
                .route(route)
                .isActive(tripRequestDTO.getIsActive())
                .build();
        tripRepository.save(trip);
    }
}
