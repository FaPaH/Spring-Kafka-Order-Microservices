package com.fapah.ordermicroservice.service;

import com.fapah.ordermicroservice.dto.OrderRequest;
import com.fapah.ordermicroservice.dto.OrderDto;
import com.fapah.ordermicroservice.entity.Order;
import com.fapah.ordermicroservice.entity.OrderItems;
import com.fapah.ordermicroservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public String save(OrderRequest orderRequest) {
        try {
            Order order = new Order();

            log.info("Creating new order");

            order.setOrderCode(UUID.randomUUID().toString());
            List<OrderItems> orderItems = orderRequest.getOrderItemsDto()
                    .stream()
                    .map(orderItemsDto -> modelMapper.map(orderItemsDto, OrderItems.class))
                    .toList();
            order.setIsCanceled(false);
            order.setIsReceived(false);
            order.setOrderItems(orderItems);

            log.info("Saving new order {}", order);

            return orderRepository.saveAndFlush(order).getOrderCode();
        } catch (RuntimeException e) {
            log.error("Failed to save order {} {}", orderRequest, e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public List<OrderDto> findAll() {
        try {
            List<Order> orders = orderRepository.findAll();
            if (orders.isEmpty()) {
                log.info("No orders found");
                return Collections.emptyList();
            }
            log.info("Found {} orders", orders.size());
            return orders.stream()
                    .map(value -> modelMapper.map(value, OrderDto.class))
                    .toList();
        } catch (RuntimeException e) {
            log.error("Failed to get all orders {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public OrderDto findByCode(String orderCode) {
        try {
            Optional<Order> order = orderRepository.findByCode(orderCode);
            log.info("Found order {}", order.get());
            return order.map(value -> modelMapper.map(value, OrderDto.class)).get();
        } catch (RuntimeException e) {
            log.warn("Failed to find order {}", orderCode);
            return null;
        }
    }

    @Override
    @Transactional
    public String delete(String orderCode) {
        try {
            orderRepository.delete(modelMapper.map(this.findByCode(orderCode), Order.class));
            return "Order deleted successfully";
        } catch (NoSuchElementException e) {
            log.warn("Failed to delete order {}", orderCode);
            return "Order not found";
        }
    }

    @Override
    @Transactional
    public String setReceived(String orderCode) {
        try {
            Order order = modelMapper.map(this.findByCode(orderCode), Order.class);
            if (order.getIsReceived()){
                log.info("Order {} is already received", order);
                return "Order is already received";
            } else if (order.getIsCanceled()){
                log.info("Order {} is canceled. Cant receive canceled orders", order);
                return "Order is canceled. Cant receive canceled orders";
            }
            order.setIsReceived(Boolean.TRUE);

           log.info("Saving received order {}", order);
            orderRepository.saveAndFlush(order);
            return "Order received successfully";
        } catch (NoSuchElementException e) {
            return "Order not found";
        }
    }

    @Override
    @Transactional
    public String setCanceled(String orderCode) {
        try {
            Order order = modelMapper.map(this.findByCode(orderCode), Order.class);
            if (order.getIsReceived()){
                log.info("Order {} is received. Cant receive canceled orders", order);
                return "Order is received. Cant receive canceled orders";
            } else if (order.getIsCanceled()){
                log.info("Order {} is already canceled", order);
                return "Order is already canceled";
            }
            order.setIsCanceled(Boolean.TRUE);

            log.info("Saving canceled order {}", order);
            orderRepository.saveAndFlush(order);
            return "Order canceled successfully";
        } catch (NoSuchElementException e) {
            return "Order not found";
        }
    }
}
