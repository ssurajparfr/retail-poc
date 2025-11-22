package com.retailcorp.retailshopping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
