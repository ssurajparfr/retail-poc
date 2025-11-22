package com.retailcorp.retailshopping.unit.service;

import com.retailcorp.retailshopping.dto.ProductResponse;
import com.retailcorp.retailshopping.entity.Product;
import com.retailcorp.retailshopping.repository.ProductRepository;
import com.retailcorp.retailshopping.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_returnsListOfProductResponses() {
        Product p1 = new Product();
        p1.setProductId(1L);
        p1.setProductName("Laptop");
        p1.setCategory("Electronics");
        p1.setBrand("BrandA");

        Product p2 = new Product();
        p2.setProductId(2L);
        p2.setProductName("Mouse");
        p2.setCategory("Electronics");
        p2.setBrand("BrandB");

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(0).getProductName()).isEqualTo("Laptop");
        assertThat(result.get(1).getProductId()).isEqualTo(2L);
        assertThat(result.get(1).getBrand()).isEqualTo("BrandB");

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void searchProducts_returnsFilteredList() {
        String query = "Laptop";

        Product p1 = new Product();
        p1.setProductId(1L);
        p1.setProductName("Laptop");
        p1.setCategory("Electronics");
        p1.setBrand("BrandA");

        when(productRepository.findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query, query))
                .thenReturn(List.of(p1));

        List<ProductResponse> result = productService.searchProducts(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("Laptop");
        assertThat(result.get(0).getBrand()).isEqualTo("BrandA");

        verify(productRepository, times(1))
                .findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query, query);
    }

    @Test
    void getProductById_existingId_returnsProductResponse() {
        Product p = new Product();
        p.setProductId(1L);
        p.setProductName("Laptop");

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Laptop");

        verify(productRepository, times(1)).findById(1L);
    }


    @Test
    void getProductById_nonExistingId_throwsResourceNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        try {
            productService.getProductById(999L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                         .hasMessageContaining("Product not found with ID: 999");
        }

        verify(productRepository, times(1)).findById(999L);
    }
}
