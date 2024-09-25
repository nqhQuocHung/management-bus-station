package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCompanyId(Long companyId);

    List<Comment> findByUserId(Long userId);
}
