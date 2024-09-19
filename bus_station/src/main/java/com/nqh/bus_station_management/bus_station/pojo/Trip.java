package com.nqh.bus_station_management.bus_station.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "bus_station_trip", schema = "bus_stationdb", catalog = "")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "depart_at", nullable = false)
    private Timestamp departAt;

    @OneToMany(mappedBy = "trip")
    private Collection<Ticket> tickets;

    @ManyToOne
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Car car;

    @ManyToOne
    @JoinColumn(name = "route_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Route route;
}
