package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.company.id = :busStationId")
    List<Car> findCarsByCompanyId(@Param("busStationId") Long busStationId);

    @Query("SELECT c FROM Car c WHERE c.company.id = :busStationId AND c.id NOT IN " +
            "(SELECT t.car.id FROM Trip t WHERE DATE(t.departAt) = :date)")
    List<Car> findAvailableCarsByCompanyAndDate(@Param("busStationId") Long busStationId, @Param("date") Date date);
}
