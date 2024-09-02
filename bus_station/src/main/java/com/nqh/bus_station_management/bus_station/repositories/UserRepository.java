package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role.id = :roleId")
    List<User> findActiveUsersByRoleId(Long roleId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId")
    long countUsersByRoleId(Long roleId);
}
