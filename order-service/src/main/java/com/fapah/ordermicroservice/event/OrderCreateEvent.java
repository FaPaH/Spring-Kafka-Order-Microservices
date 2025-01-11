package com.fapah.ordermicroservice.event;

import com.fapah.ordermicroservice.dto.OrderItemsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateEvent {

    private String orderId;

    private List<OrderItemsDto> orderItemsDto;
}
