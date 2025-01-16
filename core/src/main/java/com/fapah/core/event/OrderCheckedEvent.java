package com.fapah.core.event;

import com.fapah.core.dto.ItemsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCheckedEvent {

    private String orderId;

    private List<ItemsDto> inStockItems;

    private List<ItemsDto> outStockItems;

}
