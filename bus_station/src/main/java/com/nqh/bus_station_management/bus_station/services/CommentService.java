package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.CommentDTO;
import com.nqh.bus_station_management.bus_station.dtos.RatingDTO;
import com.nqh.bus_station_management.bus_station.pojo.Comment;

import java.util.List;

public interface CommentService {
    List<CommentDTO> getCommentsByCompanyId(Long companyId);

    Comment createComment(Comment comment);

    RatingDTO getAverageRatingByCompanyId(Long companyId);
}
