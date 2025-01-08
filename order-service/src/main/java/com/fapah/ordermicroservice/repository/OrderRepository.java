package com.fapah.ordermicroservice.repository;

import com.fapah.ordermicroservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(
            nativeQuery = true,
            value
                    = "SELECT * FROM t_order tor WHERE tor.order_code = :orderCode"
    )
    public Optional<Order> findByCode(@Param("orderCode") String orderCode);

//    @Query(
//            nativeQuery = true,
//            value
//                    = "DELETE FROM t_order tor WHERE tor.order_code = :orderCode RETURNING tor.order_code"
//    )
//    public String delete(@Param("orderCode") String orderCode);
}
