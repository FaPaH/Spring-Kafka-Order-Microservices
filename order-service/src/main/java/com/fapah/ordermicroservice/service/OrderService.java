package com.fapah.ordermicroservice.service;

import com.fapah.core.event.OrderCheckedEvent;
import com.fapah.core.event.OrderCreateEvent;
import com.fapah.ordermicroservice.dto.OrderDto;

import java.util.List;

public interface OrderService {

    String saveOrder(OrderCheckedEvent orderCheckedEvent);

    List<OrderDto> findAll();

    OrderDto findOrderByCode(String orderCode);

    String deleteOrder(String orderCode);

    String setReceived(String orderCode);

    String setCanceled(String orderCode);

    String sendOrderStockRequest(OrderCreateEvent orderCreateEvent);
}
