package com.retailcorp.retailshopping.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSearchResponse {
    private Long productId;
    private String productName;
    private Double unitPrice;
}
