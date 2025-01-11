package com.fapah.ordermicroservice.handler;

import com.fapah.ordermicroservice.event.OrderCreateEvent;
import com.fapah.ordermicroservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(topics = "order-checked-events-topic")
public class OrderRequestHandler {

    private final OrderService orderService;

    @KafkaHandler
    public void handle(OrderCreateEvent orderCreateEvent) {
        log.info("Received checked order response: {}", orderCreateEvent.getOrderId());
        orderService.save(orderCreateEvent);
    }
}
