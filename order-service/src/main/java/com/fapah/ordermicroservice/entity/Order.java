package com.fapah.ordermicroservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "order_is_received", nullable = false)
    private Boolean isReceived;

    @Column(name = "order_is_canceled", nullable = false)
    private Boolean isCanceled;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItems> orderItems;
}
