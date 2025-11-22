package com.retailcorp.retailshopping.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderRequest {
    private Long customerId;
    private List<OrderItemRequest> items;
    private String paymentMethod;
    private String shippingAddress;
}
