package com.nqh.bus_station_management.bus_station.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "bus_station_cargo", schema = "bus_stationdb", catalog = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "receiver_name", nullable = false, length = 255)
    private String receiverName;

    @NotNull
    @Size(max = 50)
    @Column(name = "receiver_email", nullable = false, length = 50)
    private String receiverEmail;

    @NotNull
    @Size(max = 20)
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @NotNull
    @Size(max = 255)
    @Column(name = "receiver_address", nullable = false, length = 255)
    private String receiverAddress;

    @NotNull
    @Min(0)
    @Column(name = "cargo_price", nullable = false, precision = 0)
    private Double cargoPrice;

    @Column(name = "sent_at")
    private Timestamp sentAt;

    @NotNull
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
    private Ticket ticket;
}
