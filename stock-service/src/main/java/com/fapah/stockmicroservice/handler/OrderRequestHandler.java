package com.fapah.stockmicroservice.handler;

import com.fapah.core.event.OrderCreateEvent;
import com.fapah.stockmicroservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(topics = "order-created-events-topic")
public class OrderRequestHandler {

    private final StockService stockService;

    @KafkaHandler
    public void handle(OrderCreateEvent orderCreateEvent) {
        log.info("Received order to check: {}", orderCreateEvent.getOrderId());
        stockService.sendOrderCheckedEvent(stockService.checkStock(orderCreateEvent));
    }
}
