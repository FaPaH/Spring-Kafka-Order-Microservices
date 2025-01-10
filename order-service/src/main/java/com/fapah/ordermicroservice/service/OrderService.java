package com.fapah.ordermicroservice.service;

import com.fapah.ordermicroservice.dto.OrderRequest;
import com.fapah.ordermicroservice.dto.OrderDto;

import java.util.List;

public interface OrderService {

    String save(OrderRequest orderRequest);

    List<OrderDto> findAll();

    OrderDto findByCode(String orderCode);

    String delete(String orderCode);

    String setReceived(String orderCode);

    String setCanceled(String orderCode);

    void sendMessage(OrderRequest orderRequest);
}
