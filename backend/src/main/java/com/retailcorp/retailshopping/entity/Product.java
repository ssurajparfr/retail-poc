package com.retailcorp.retailshopping.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    private Long productId;
    private String productName;
    private String category;
    private String subcategory;
    private String brand;
    private Double unitPrice;
    private Double costPrice;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Boolean isActive;
    private LocalDate createdDate;
    private LocalDateTime lastUpdated;
}

