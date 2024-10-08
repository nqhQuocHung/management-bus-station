package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.dtos.CompanyDTO;
import com.nqh.bus_station_management.bus_station.dtos.CompanyDTO;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<TransportationCompany, Long> {

    @Query("SELECT c FROM TransportationCompany c WHERE :name IS NULL OR c.name LIKE CONCAT(:name, '%')")
    List<TransportationCompany> list(@Param("name") String name);

    @Query("SELECT COUNT(c) FROM TransportationCompany c WHERE :name IS NULL OR c.name LIKE CONCAT(:name, '%')")
    Long count(@Param("name") String name);

    List<TransportationCompany> findByIsVerifiedFalse();

    @Query("SELECT c FROM TransportationCompany c WHERE (:name IS NULL OR c.name LIKE CONCAT(:name, '%')) AND c.isVerified = true")
    List<TransportationCompany> getListVerified(@Param("name") String name);

    TransportationCompany findByManagerId(Long id);

    @Query("SELECT new com.nqh.bus_station_management.bus_station.dtos.CompanyDTO(tc.id, tc.name, tc.avatar, tc.phone, tc.email, tc.isVerified, tc.isActive, tc.isCargoTransport, tc.manager.id) FROM TransportationCompany tc WHERE tc.id = :companyId")
    CompanyDTO getCompanyAndManager(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(tc) FROM TransportationCompany tc")
    long countAllCompanies();

    @Query("SELECT COUNT(tc) FROM TransportationCompany tc WHERE tc.isVerified = true")
    long countVerifiedCompanies();

    @Query("SELECT COUNT(tc) FROM TransportationCompany tc WHERE tc.isActive = true")
    long countActiveCompanies();
}
