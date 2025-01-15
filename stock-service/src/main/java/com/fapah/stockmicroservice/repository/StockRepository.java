package com.fapah.stockmicroservice.repository;

import com.fapah.stockmicroservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StockRepository extends JpaRepository<Product, Long> {

    Product getProductsByName(String name);

    void deleteProductByName(String name);

    List<Product> findAllByOrderByNameAsc();

    @Modifying
    @Query("UPDATE Product tor set tor.price = :productPrice, tor.quantity = :productQuantity where tor.name = :productName")
    void updateProductPriceByName(@Param("productName") String name,
                                  @Param("productPrice") BigDecimal price,
                                  @Param("productQuantity") int quantity);
}
