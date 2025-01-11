package com.fapah.core.event;

import com.fapah.core.dto.ItemsDto;

import java.util.List;

public class OrderCheckedEvent {

    private String orderId;

    private List<ItemsDto> inStockItems;

    private List<ItemsDto> outStockItems;

    public OrderCheckedEvent() {
    }

    public OrderCheckedEvent(String orderId, List<ItemsDto> inStockItems, List<ItemsDto> outStockItems) {
        this.orderId = orderId;
        this.inStockItems = inStockItems;
        this.outStockItems = outStockItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<ItemsDto> getInStockItems() {
        return inStockItems;
    }

    public void setInStockItems(List<ItemsDto> inStockItems) {
        this.inStockItems = inStockItems;
    }

    public List<ItemsDto> getOutStockItems() {
        return outStockItems;
    }

    public void setOutStockItems(List<ItemsDto> outStockItems) {
        this.outStockItems = outStockItems;
    }
}
