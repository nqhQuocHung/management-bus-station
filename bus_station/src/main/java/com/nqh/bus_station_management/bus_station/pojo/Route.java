package com.nqh.bus_station_management.bus_station.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "bus_station_route", schema = "bus_stationdb", catalog = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Basic
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Basic
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Basic
    @Column(name = "seat_price", nullable = false, precision = 0)
    private Double seatPrice;

    @Basic
    @Column(name = "cargo_price", nullable = false, precision = 0)
    private Double cargoPrice;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private TransportationCompany company;

    @OneToMany(mappedBy = "route", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Collection<Trip> trips;

    @ManyToOne
    @JoinColumn(name = "from_station", nullable = false)
    private Station fromStation;

    @ManyToOne
    @JoinColumn(name = "to_station", nullable = false)
    private Station toStation;
}
