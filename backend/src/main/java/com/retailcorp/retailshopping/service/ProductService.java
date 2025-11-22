package com.retailcorp.retailshopping.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retailcorp.retailshopping.dto.ProductResponse;
import com.retailcorp.retailshopping.entity.Product;
import com.retailcorp.retailshopping.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> searchProducts(String query) {
        return productRepository
                .findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query, query)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return toResponse(product);
    }

    public ProductResponse toResponse(Product product) {
    return ProductResponse.builder()
            .productId(product.getProductId())
            .productName(product.getProductName())
            .category(product.getCategory())
            .subcategory(product.getSubcategory())
            .brand(product.getBrand())
            .unitPrice(product.getUnitPrice())
            .stockQuantity(product.getStockQuantity())
            .reorderLevel(product.getReorderLevel())
            .build();
}

}



