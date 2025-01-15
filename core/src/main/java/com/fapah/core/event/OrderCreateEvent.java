package com.fapah.core.event;

import com.fapah.core.dto.ItemsDto;

import java.util.List;

public class OrderCreateEvent {

    private String orderId;

    private List<ItemsDto> orderItemsDto;

    public OrderCreateEvent() {
    }

    public OrderCreateEvent(String orderId, List<ItemsDto> orderItemsDto) {
        this.orderId = orderId;
        this.orderItemsDto = orderItemsDto;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<ItemsDto> getOrderItemsDto() {
        return orderItemsDto;
    }

    public void setOrderItemsDto(List<ItemsDto> orderItemsDto) {
        this.orderItemsDto = orderItemsDto;
    }
}
