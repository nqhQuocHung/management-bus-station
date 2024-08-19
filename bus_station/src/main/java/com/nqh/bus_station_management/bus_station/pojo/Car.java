package com.nqh.bus_station_management.bus_station.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
@Table(name = "bus_station_car", schema = "bus_stationdb", catalog = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Car {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @NotNull(message = "{car.carNumber.notNull}")
    @Size(min = 0, max = 10, message = "{car.carNumber.size}")
    @Column(name = "car_number", nullable = false, length = 10)
    private String carNumber;

    @OneToMany(mappedBy = "car")
    private Collection<Seat> seats;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private TransportationCompany company;
}
