package com.fapah.ordermicroservice.service;

import com.fapah.ordermicroservice.dto.OrderRequest;
import com.fapah.ordermicroservice.dto.OrderDto;

import java.util.List;

public interface OrderService {

    public String save(OrderRequest orderRequest);

    public List<OrderDto> findAll();

    public OrderDto findByCode(String orderCode);

    public String delete(String orderCode);

    public String setReceived(String orderCode);

    public String setCanceled(String orderCode);
}
