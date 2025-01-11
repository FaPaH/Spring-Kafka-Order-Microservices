package com.fapah.ordermicroservice.service;

import com.fapah.ordermicroservice.event.OrderCreateEvent;
import com.fapah.ordermicroservice.dto.OrderDto;

import java.util.List;

public interface OrderService {

    String save(OrderCreateEvent orderCreateEvent);

    List<OrderDto> findAll();

    OrderDto findByCode(String orderCode);

    String delete(String orderCode);

    String setReceived(String orderCode);

    String setCanceled(String orderCode);

    String sendOrderStockRequest(OrderCreateEvent orderCreateEvent);
}
