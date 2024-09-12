package com.busstation.dtos;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class TripRequestDTO {
    private String departAt;
    private Boolean isActive;
    private Long carId;
    private Long routeId;
}
