package com.fapah.ordermicroservice.dto;

import com.fapah.ordermicroservice.entity.OrderItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String orderCode;

    private Boolean isReceived;

    private Boolean isCanceled;

    private List<OrderItems> orderItems;
}
