package com.busstation.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDetailDTO {
    private Long id;
    private String name;
    private Long companyId;
    private Double seatPrice;
    private Double cargoPrice;
    private Boolean isActive;
    private Long fromStationId;
    private Long toStationId;
}
