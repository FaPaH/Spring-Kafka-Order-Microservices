package com.fapah.stockmicroservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name",nullable = false, unique = true)
    private String name;

    @Column(name="product_price", nullable = false)
    private BigDecimal price;

    @Column(name = "product_quantity", nullable = false)
    private int quantity;

}
