package com.busstation.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "bus_station_paymentmethod", schema = "bus-station-db", catalog = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "name", nullable = false, length = 10)
    private String name;
    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<Ticket> tickets;


}
