package com.nqh.bus_station_management.bus_station.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "bus_station_paymentmethod", schema = "bus_stationdb", catalog = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<Ticket> tickets;
}
