package com.nqh.bus_station_management.bus_station.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    @NotNull
    private Long userId;
    private Long companyId;
    @NotNull
    private String content;
    @NotNull
    private Integer rating;
    private String avatar;
    private Timestamp createdAt;
    private String firstname;
    private String lastname;
}
