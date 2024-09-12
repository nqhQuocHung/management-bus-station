package com.busstation.dtos;

import com.busstation.pojo.Station;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Long id;
    private String name;
    private CompanyPublicDTO company;
    private Double seatPrice;
    private Double cargoPrice;
    private Station fromStation;
    private Station toStation;
}
