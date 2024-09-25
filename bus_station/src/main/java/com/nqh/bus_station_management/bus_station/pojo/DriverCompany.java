package com.nqh.bus_station_management.bus_station.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "bus_station_driver_company", schema = "bus_stationdb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private TransportationCompany company;

    @Column(name = "verified", nullable = false)
    private Boolean verified;
}

