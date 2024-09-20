package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.car.id = :carId AND s.id NOT IN " +
            "(SELECT t.seat.id FROM Ticket t WHERE t.trip.id = :tripId)")
    List<Seat> findAvailableSeatsByTripId(Long tripId, Long carId);

    @Query("SELECT s FROM Seat s WHERE s.car.id = :carId AND s.id IN " +
            "(SELECT t.seat.id FROM Ticket t WHERE t.trip.id = :tripId)")
    List<Seat> findOccupiedSeatsByTripId(@Param("tripId") Long tripId, @Param("carId") Long carId);
}
