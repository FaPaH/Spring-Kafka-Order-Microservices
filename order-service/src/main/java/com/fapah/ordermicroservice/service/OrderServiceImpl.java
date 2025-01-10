package com.fapah.ordermicroservice.service;

import com.fapah.ordermicroservice.dto.OrderDto;
import com.fapah.ordermicroservice.dto.OrderRequest;
import com.fapah.ordermicroservice.entity.Order;
import com.fapah.ordermicroservice.entity.OrderItems;
import com.fapah.ordermicroservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;

    @Override
    @Transactional
    public String save(OrderRequest orderRequest) {
        try {
            log.info("Creating new order");

            Order order = new Order();
            order.setOrderCode(orderRequest.getOrderId());
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
            log.warn("Failed to save order {}, {}", orderRequest, e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    @Transactional(readOnly = true)
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
            log.warn("Failed to get all orders {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
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
            if (order.getIsReceived()) {
                log.info("Order {} is already received", order);
                return "Order is already received";
            } else if (order.getIsCanceled()) {
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
            if (order.getIsReceived()) {
                log.info("Order {} is received. Cant receive canceled orders", order);
                return "Order is received. Cant receive canceled orders";
            } else if (order.getIsCanceled()) {
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

    @Override
    @Transactional
    public void sendMessage(OrderRequest orderRequest) {
        try {
            String orderKey = UUID.randomUUID().toString();

            orderRequest.setOrderId(orderKey);
            SendResult<String, OrderRequest> result = kafkaTemplate
                    .send("order-created-events-topic", orderKey, orderRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
    }
}
