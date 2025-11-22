package com.retailcorp.retailshopping.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String productName;
    private String category;
    private String subcategory;
    private String brand;
    private Double unitPrice;
    private Integer stockQuantity;
    private Integer reorderLevel;
}
