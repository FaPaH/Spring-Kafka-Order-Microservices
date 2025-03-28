package com.fapah.ordermicroservice.service;

import com.fapah.core.dto.ItemsDto;
import com.fapah.core.event.OrderCheckedEvent;
import com.fapah.core.event.OrderCreateEvent;
import com.fapah.ordermicroservice.dto.OrderDto;
import com.fapah.ordermicroservice.entity.Order;
import com.fapah.ordermicroservice.entity.OrderItems;
import com.fapah.ordermicroservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
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

    private final KafkaTemplate<String, OrderCreateEvent> kafkaTemplate;

    @Override
    @Transactional
    public String saveOrder(OrderCheckedEvent orderCheckedEvent) {
        try {
            log.info("Creating new order");

            Order order = Order.builder()
                    .orderCode(orderCheckedEvent.getOrderId())
                    .orderItems(dtoToEntity(orderCheckedEvent.getInStockItems()))
                    .isCanceled(Boolean.FALSE)
                    .isReceived(Boolean.FALSE)
                    .build();

            log.info("Saving new order {}", order);

            return orderRepository.saveAndFlush(order).getOrderCode();
        } catch (RuntimeException e) {
            log.error("Failed to save order {}, {}", orderCheckedEvent, e.getMessage());
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
            log.error("Failed to get all orders {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findOrderByCode(String orderCode) {
        try {
            Optional<Order> order = orderRepository.findByCode(orderCode);
            log.info("Found order {}", order.get());
            return order.map(value -> modelMapper.map(value, OrderDto.class)).get();
        } catch (RuntimeException e) {
            log.error("Failed to find order {}", orderCode);
            return null;
        }
    }

    @Override
    @Transactional
    public String deleteOrder(String orderCode) {
        try {
            orderRepository.delete(modelMapper.map(this.findOrderByCode(orderCode), Order.class));
            return "Order deleted successfully";
        } catch (NoSuchElementException e) {
            log.warn("No such element with code {} in db", orderCode, e);
            return "Order not found";
        }
    }

    @Override
    @Transactional
    public String setReceived(String orderCode) {
        try {
            Order order = modelMapper.map(this.findOrderByCode(orderCode), Order.class);
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
            log.warn("No such element with code {} in db", orderCode, e);
            return "Order not found";
        }
    }

    @Override
    @Transactional
    public String setCanceled(String orderCode) {
        try {
            Order order = modelMapper.map(this.findOrderByCode(orderCode), Order.class);
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
            log.warn("No such element with code {} in db", orderCode, e);
            return "Order not found";
        }
    }

    @Override
    @Transactional
    public String sendOrderStockRequest(OrderCreateEvent orderCreateEvent) {
        try {
            String orderKey = UUID.randomUUID().toString();

            orderCreateEvent.setOrderId(orderKey);

            log.info("Sending order event {}", orderCreateEvent);

            ProducerRecord<String, OrderCreateEvent> record = new ProducerRecord<>(
                    "order-created-events-topic",
                    orderKey,
                    orderCreateEvent
            );

            record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

            SendResult<String, OrderCreateEvent> result = kafkaTemplate
                    .send(record).get();

            log.info("Event sent successfully {}", result.getRecordMetadata());

            return result.getProducerRecord().value().getOrderId();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send order event {} {}", orderCreateEvent, e.getMessage());
            return "Oops! Something went wrong. Try again later";
        }
    }

    private List<OrderItems> dtoToEntity(List<ItemsDto> orderItemsDtoList) {
        try {
            return orderItemsDtoList.stream()
                    .map(orderItemsDto -> modelMapper.map(orderItemsDto, OrderItems.class))
                    .toList();
        } catch (RuntimeException e) {
            log.error("Failed to map dto {} to entity {}", orderItemsDtoList, e.getMessage());
            return null;
        }
    }
}
