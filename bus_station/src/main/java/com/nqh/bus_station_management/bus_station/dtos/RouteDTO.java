package com.nqh.bus_station_management.bus_station.dtos;
import com.nqh.bus_station_management.bus_station.pojo.Station;
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
    private Boolean isCargo;
    private Double seatPrice;
    private Double cargoPrice;
    private Station fromStation;
    private Station toStation;
}
