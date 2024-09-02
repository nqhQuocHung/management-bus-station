package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    Optional<Role> findById(Long id);
}
