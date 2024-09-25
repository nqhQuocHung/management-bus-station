package com.nqh.bus_station_management.bus_station.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "online_payment_result", schema = "bus_stationdb")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OnlinePaymentResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "payment_code", nullable = false)
    private String paymentCode;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "transaction_no", nullable = false)
    private String transactionNo;

    @Column(name = "bank_transaction_no", nullable = false)
    private String bankTransactionNo;

    @Column(name = "card_type", nullable = false)
    private String cardType;

    @Column(name = "confirm_at")
    private Timestamp confirmAt;

    @OneToMany(mappedBy = "paymentResult", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Ticket> tickets;

}
