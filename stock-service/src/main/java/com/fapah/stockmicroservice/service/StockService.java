package com.fapah.stockmicroservice.service;

import com.fapah.core.event.OrderCheckedEvent;
import com.fapah.core.event.OrderCreateEvent;
import com.fapah.stockmicroservice.entity.Product;
import com.fapah.stockmicroservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface StockService {

    Product findByName(String name);

    String deleteByName(String name);

    String saveProduct(Product product);

    List<Product> findAll();

    String updateProduct(Product product);

    OrderCheckedEvent checkStock(OrderCreateEvent order);

    void sendOrderCheckedEvent(OrderCheckedEvent orderCheckedEvent);
}
