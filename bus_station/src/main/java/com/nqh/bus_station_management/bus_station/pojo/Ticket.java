package com.nqh.bus_station_management.bus_station.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "bus_station_ticket", schema = "bus_stationdb", catalog = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @NotNull(message = "{ticket.seatPrice.notNull}")
    @Min(value = 0, message = "{ticket.seatPrice.min}")
    @Column(name = "seat_price", nullable = false, precision = 0)
    private Double seatPrice;

    @Column(name = "paid_at")
    private Timestamp paidAt;

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cargo cargo;

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", referencedColumnName = "id", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "seat_id", referencedColumnName = "id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE) // Adjusted cascade type
    @JoinColumn(name = "payment_id")
    private OnlinePaymentResult paymentResult;
}
