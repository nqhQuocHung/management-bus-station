package com.busstation.dtos;

import com.busstation.pojo.Seat;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Data
public class TripDTO {
    private Long id;
    private Timestamp departAt;
}
