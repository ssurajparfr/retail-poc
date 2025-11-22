package com.retailcorp.retailshopping.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private Long customerId;

    private String orderStatus;
    private String paymentMethod;
    private String shippingAddress;

    private Double totalAmount;
    private LocalDateTime orderDate;

    private List<OrderItemResponse> items;
}
