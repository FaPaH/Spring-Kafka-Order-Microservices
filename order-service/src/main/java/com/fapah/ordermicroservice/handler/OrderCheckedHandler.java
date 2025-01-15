package com.fapah.ordermicroservice.handler;

import com.fapah.core.event.OrderCheckedEvent;
import com.fapah.core.event.OrderCreateEvent;
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
public class OrderCheckedHandler {

    private final OrderService orderService;

    @KafkaHandler
    public void handle(OrderCheckedEvent orderCheckedEvent) {
        log.info("Received checked order response: {}", orderCheckedEvent.getOrderId());
        orderService.save(orderCheckedEvent);
    }
}
