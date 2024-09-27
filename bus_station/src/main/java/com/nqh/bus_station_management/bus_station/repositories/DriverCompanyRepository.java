package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.DriverCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface DriverCompanyRepository extends JpaRepository<DriverCompany, Long> {

    @Query("SELECT dc FROM DriverCompany dc WHERE dc.user.id = :driverId")
    DriverCompany findByDriverId(@Param("driverId") Long driverId);


    @Query("SELECT dc FROM DriverCompany dc WHERE dc.company.id = :companyId")
    List<DriverCompany> findDriversByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT dc FROM DriverCompany dc WHERE dc.company.id = :companyId AND dc.verified = true")
    List<DriverCompany> findVerifiedDriversByCompanyId(@Param("companyId") Long companyId);

    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

    @Query("SELECT d FROM DriverCompany d WHERE d.company.id = :companyId AND d.user.id NOT IN " +
            "(SELECT t.driver.id FROM Trip t WHERE DATE(t.departAt) = :date AND t.status = false)")
    List<DriverCompany> findAvailableDriversByCompanyAndDate(@Param("companyId") Long companyId, @Param("date") Date date);

}
