package com.nqh.bus_station_management.bus_station.repositories;
import com.nqh.bus_station_management.bus_station.pojo.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.company.id = :busStationId")
    List<Car> findCarsByCompanyId(@Param("busStationId") Long busStationId);
}

