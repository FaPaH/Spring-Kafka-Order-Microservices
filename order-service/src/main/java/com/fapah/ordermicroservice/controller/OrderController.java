package com.fapah.ordermicroservice.controller;

import com.fapah.ordermicroservice.dto.OrderCreateEvent;
import com.fapah.ordermicroservice.dto.OrderDto;
import com.fapah.ordermicroservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<String> createOrder(@RequestBody OrderCreateEvent orderCreateEvent) {
        log.info("Create order request: {}", orderCreateEvent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Checking stock for available goods. Your order id: "
                        + orderService.sendOrderStockRequest(orderCreateEvent));
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        log.info("Get all orders");
        return ResponseEntity.ok().body(orderService.findAll());
    }

    @GetMapping("/getOrderByCode")
    public ResponseEntity<OrderDto> getOrderByCode(@RequestParam String orderCode) {
        MDC.put("requestId", orderCode);
        log.info("Get order by code: {}", orderCode);
        if (orderCode.isBlank()) {
            log.warn("Order code is blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(orderService.findByCode(orderCode));
    }

    @PostMapping("/deleteOrderByCode")
    public ResponseEntity<String> deleteOrderByCode(@RequestParam String orderCode) {
        MDC.put("requestId", orderCode);
        log.info("Delete order by code: {}", orderCode);
        if (orderCode.isBlank()) {
            log.warn("Order code is blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(orderService.delete(orderCode));
    }

    @PostMapping("setReceived")
    public ResponseEntity<String> setReceived(@RequestParam String orderCode) {
        MDC.put("requestId", orderCode);
        log.info("Set order received: {}", orderCode);
        return ResponseEntity.ok().body(orderService.setReceived(orderCode));
    }

    @PostMapping("setCanceled")
    public ResponseEntity<String> setCanceled(@RequestParam String orderCode) {
        MDC.put("requestId", orderCode);
        log.info("Set order canceled: {}", orderCode);
        return ResponseEntity.ok().body(orderService.setCanceled(orderCode));
    }
}
