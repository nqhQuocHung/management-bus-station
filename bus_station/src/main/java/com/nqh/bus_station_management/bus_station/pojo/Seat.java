package com.nqh.bus_station_management.bus_station.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "bus_station_seats", schema = "bus_stationdb", catalog = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "code_id", referencedColumnName = "id", nullable = false)
    private Code seatCode;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    private Car car;
}
