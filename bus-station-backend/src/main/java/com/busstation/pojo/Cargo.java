package com.busstation.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "bus_station_cargo", schema = "bus-station-db", catalog = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cargo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "receiver_name", nullable = false, length = 255)
    private String receiverName;
    @Basic
    @Column(name = "receiver_email", nullable = false, length = 50)
    private String receiverEmail;
    @Basic
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;
    @Basic
    @Column(name = "receiver_address", nullable = false, length = 255)
    private String receiverAddress;
    @Basic
    @Column(name = "cargo_price", nullable = false, precision = 0)
    private Double cargoPrice;
    @Basic
    @Column(name = "sent_at")
    private Timestamp sentAt;
    @Basic
    @Column(name = "description", nullable = false, length = -1)
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
    private Ticket ticket;
}
