package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
