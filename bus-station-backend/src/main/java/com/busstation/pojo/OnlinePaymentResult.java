package com.busstation.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "online_payment_result")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class OnlinePaymentResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="payment_id")
    private Long id;

    @Column(name = "payment_code")
    private String paymentCode;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "bank_transaction_no")
    private String bankTransactionNo;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "confirm_at")
    private Timestamp confirmAt;

    @OneToMany(mappedBy = "paymentResult", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Ticket> tickets;

}
