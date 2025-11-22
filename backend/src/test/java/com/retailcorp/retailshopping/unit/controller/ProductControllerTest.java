package com.retailcorp.retailshopping.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcorp.retailshopping.controller.ProductController;
import com.retailcorp.retailshopping.dto.ProductResponse;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.repository.ProductRepository;
import com.retailcorp.retailshopping.service.JwtTokenProvider;
import com.retailcorp.retailshopping.service.ProductService;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // disables security for unit tests
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CustomerRepository customerRepository;
    
    @Test
    void getProducts_returnsListOfProducts() throws Exception {
        ProductResponse p1 = ProductResponse.builder()
                .productId(1L)
                .productName("Laptop")
                .category("Electronics")
                .brand("BrandA")
                .unitPrice(1200.0)
                .stockQuantity(50)
                .build();

        ProductResponse p2 = ProductResponse.builder()
                .productId(2L)
                .productName("Phone")
                .category("Electronics")
                .brand("BrandB")
                .unitPrice(800.0)
                .stockQuantity(100)
                .build();

        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("Laptop"))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].productName").value("Phone"));
    }

    @Test
    void searchProducts_returnsMatchingProducts() throws Exception {
        ProductResponse p1 = ProductResponse.builder()
                .productId(1L)
                .productName("Laptop Pro")
                .category("Electronics")
                .brand("BrandA")
                .unitPrice(1500.0)
                .stockQuantity(30)
                .build();

        when(productService.searchProducts(anyString())).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/products/search")
                        .param("query", "Laptop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("Laptop Pro"));
    }
}
