package com.busstation.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bus_station_seat", schema = "bus-station-db", catalog = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "code", nullable = false, length = 5)
    private String code;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    private Car car;


}
