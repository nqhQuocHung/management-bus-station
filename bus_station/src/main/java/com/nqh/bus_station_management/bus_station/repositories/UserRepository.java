package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsUserDTO;
import com.nqh.bus_station_management.bus_station.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role.id = :roleId")
    List<User> findActiveUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT new com.nqh.bus_station_management.bus_station.dtos.StatisticsUserDTO(u.role.name, COUNT(u)) " +
            "FROM User u GROUP BY u.role.name")
    List<StatisticsUserDTO> countUsersByRole();

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(@Param("email") String email);


    Optional<User> findByUsernameAndEmail(String username, String email);
}
