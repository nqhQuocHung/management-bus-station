package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.dtos.CommentDTO;
import com.nqh.bus_station_management.bus_station.dtos.RatingDTO;
import com.nqh.bus_station_management.bus_station.pojo.Comment;
import com.nqh.bus_station_management.bus_station.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

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
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        try {
            Comment createdComment = commentService.createComment(comment);
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
