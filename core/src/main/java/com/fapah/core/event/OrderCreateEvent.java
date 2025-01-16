package com.fapah.core.event;

import com.fapah.core.dto.ItemsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateEvent {

    private String orderId;

    private List<ItemsDto> orderItemsDto;

}
