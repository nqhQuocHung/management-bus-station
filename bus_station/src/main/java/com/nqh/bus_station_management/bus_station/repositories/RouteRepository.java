package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {


    @Query("SELECT r FROM Route r JOIN FETCH r.company c JOIN FETCH r.fromStation fs JOIN FETCH r.toStation ts " +
            "WHERE (:name IS NULL OR r.name LIKE %:name%) " +
            "AND (:isActive IS NULL OR r.isActive = :isActive)")
    Page<Route> list(@Param("name") String name,
                     @Param("isActive") Boolean isActive,
                     Pageable pageable);


    @Query("SELECT COUNT(r) FROM Route r WHERE (:name IS NULL OR r.name LIKE %:name%) " +
            "AND (:isActive IS NULL OR r.isActive = :isActive)")
    Long count(@Param("name") String name, @Param("isActive") Boolean isActive);


    @Query("SELECT r FROM Route r WHERE r.id = :id")
    Route getById(@Param("id") Long id);

    @Query("SELECT r FROM Route r WHERE r.company.id = :companyId")
    List<Route> findByCompanyId(@Param("companyId") Long companyId);
}
