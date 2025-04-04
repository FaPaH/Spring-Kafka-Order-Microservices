package com.fapah.ordermicroservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_product")
@Builder
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name="product_price", nullable = false)
    private BigDecimal price;

    @Column(name = "product_quantity", nullable = false)
    private int quantity;
}
