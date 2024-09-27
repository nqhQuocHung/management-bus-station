package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.CommentCreateDTO;
import com.nqh.bus_station_management.bus_station.dtos.CommentDTO;
import com.nqh.bus_station_management.bus_station.dtos.RatingDTO;
import com.nqh.bus_station_management.bus_station.pojo.Comment;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.repositories.CommentRepository;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import com.nqh.bus_station_management.bus_station.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        User user = userRepository.findById(comment.getUser().getId()).orElse(null);
        return CommentDTO.builder()
                .userId(comment.getUser().getId())
                .companyId(comment.getCompany().getId())
                .content(comment.getContent())
                .rating(comment.getRating())
                .createdAt(comment.getCreatedAt())
                .avatar(user != null ? user.getAvatar() : null)
                .firstname(user != null ? user.getFirstname() : null)
                .lastname(user != null ? user.getLastname() : null)
                .build();
    }

    @Override
    public List<CommentDTO> getCommentsByCompanyId(Long companyId) {
        List<Comment> comments = commentRepository.findByCompanyId(companyId);
        return comments.stream()
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
    }

    public Comment createComment(CommentCreateDTO commentCreateDTO, User user, TransportationCompany company) {
        Comment comment = Comment.builder()
                .content(commentCreateDTO.getContent())
                .rating(commentCreateDTO.getRating())
                .user(user)
                .company(company)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public RatingDTO getAverageRatingByCompanyId(Long companyId) {
        List<Comment> comments = commentRepository.findByCompanyId(companyId);
        if (comments.isEmpty()) {
            return new RatingDTO(0.0, 0);
        }
        double totalRating = comments.stream().mapToInt(Comment::getRating).sum();
        long totalReviews = comments.size();
        double averageRating = totalRating / totalReviews;
        return new RatingDTO(averageRating, totalReviews);
    }
}
