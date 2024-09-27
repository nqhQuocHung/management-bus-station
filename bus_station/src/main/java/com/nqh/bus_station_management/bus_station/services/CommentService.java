package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.dtos.CommentCreateDTO;
import com.nqh.bus_station_management.bus_station.dtos.CommentDTO;
import com.nqh.bus_station_management.bus_station.dtos.RatingDTO;
import com.nqh.bus_station_management.bus_station.pojo.Comment;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;

import java.util.List;

public interface CommentService {
    List<CommentDTO> getCommentsByCompanyId(Long companyId);

    Comment createComment(CommentCreateDTO commentCreateDTO, User user, TransportationCompany company);

    RatingDTO getAverageRatingByCompanyId(Long companyId);
}
