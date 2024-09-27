package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.CommentCreateDTO;
import com.nqh.bus_station_management.bus_station.dtos.CommentDTO;
import com.nqh.bus_station_management.bus_station.dtos.RatingDTO;
import com.nqh.bus_station_management.bus_station.pojo.Comment;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.services.CommentService;
import com.nqh.bus_station_management.bus_station.services.CompanyService;
import com.nqh.bus_station_management.bus_station.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private  CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;


    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByCompanyId(@PathVariable Long companyId) {
        List<CommentDTO> comments = commentService.getCommentsByCompanyId(companyId);
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/rating/{companyId}")
    public ResponseEntity<RatingDTO> getAverageRating(@PathVariable Long companyId) {
        RatingDTO ratingDTO = commentService.getAverageRatingByCompanyId(companyId);
        return ResponseEntity.ok(ratingDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        try {
            Optional<User> optionalUser = userService.findUserById(commentCreateDTO.getUserId());
            if (!optionalUser.isPresent()) {
                return new ResponseEntity<>("Người dùng không tồn tại.", HttpStatus.BAD_REQUEST);
            }
            User user = optionalUser.get();

            Optional<TransportationCompany> optionalCompany = companyService.getCompanyById(commentCreateDTO.getCompanyId());
            if (!optionalCompany.isPresent()) {
                return new ResponseEntity<>("Công ty không tồn tại.", HttpStatus.BAD_REQUEST);
            }
            TransportationCompany company = optionalCompany.get();

            Comment createdComment = commentService.createComment(commentCreateDTO, user, company);
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Đã xảy ra lỗi khi tạo đánh giá.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
