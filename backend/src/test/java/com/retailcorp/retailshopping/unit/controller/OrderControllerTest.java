package com.retailcorp.retailshopping.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcorp.retailshopping.controller.OrderController;
import com.retailcorp.retailshopping.dto.OrderItemRequest;
import com.retailcorp.retailshopping.dto.OrderItemResponse;
import com.retailcorp.retailshopping.dto.OrderRequest;
import com.retailcorp.retailshopping.dto.OrderResponse;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.service.JwtTokenProvider;
import com.retailcorp.retailshopping.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void checkout_success_returnsOrderResponse() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(42L);
        request.setPaymentMethod("Credit Card");
        request.setShippingAddress("123 Main St");
        request.setItems(List.of(new OrderItemRequest(101L, 1)));

        OrderResponse response = OrderResponse.builder()
                .orderId(1L)
                .customerId(42L)
                .orderStatus("CONFIRMED")
                .paymentMethod("Credit Card")
                .shippingAddress("123 Main St")
                .totalAmount(100.0)
                .orderDate(LocalDateTime.now())
                .items(List.of(OrderItemResponse.builder()
                        .productId(101L)
                        .productName("Test Product")
                        .unitPrice(100.0)
                        .quantity(1)
                        .discountPercent(0.0)
                        .lineTotal(100.0)
                        .build()))
                .build();

        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.customerId").value(42))
                .andExpect(jsonPath("$.orderStatus").value("CONFIRMED"))
                .andExpect(jsonPath("$.items[0].productId").value(101))
                .andExpect(jsonPath("$.items[0].productName").value("Test Product"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(100.0))
                .andExpect(jsonPath("$.items[0].quantity").value(1))
                .andExpect(jsonPath("$.items[0].discountPercent").value(0.0))
                .andExpect(jsonPath("$.items[0].lineTotal").value(100.0));
    }

    @Test
    void getOrdersByCustomer_success_returnsList() throws Exception {
        OrderResponse order1 = OrderResponse.builder()
                .orderId(1L)
                .customerId(42L)
                .orderStatus("CONFIRMED")
                .paymentMethod("Credit Card")
                .shippingAddress("123 Main St")
                .totalAmount(100.0)
                .orderDate(LocalDateTime.now())
                .items(List.of(OrderItemResponse.builder()
                        .productId(101L)
                        .productName("Test Product")
                        .unitPrice(100.0)
                        .quantity(1)
                        .discountPercent(0.0)
                        .lineTotal(100.0)
                        .build()))
                .build();

        OrderResponse order2 = OrderResponse.builder()
                .orderId(2L)
                .customerId(42L)
                .orderStatus("SHIPPED")
                .paymentMethod("PayPal")
                .shippingAddress("456 Oak Ave")
                .totalAmount(200.0)
                .orderDate(LocalDateTime.now())
                .items(List.of(OrderItemResponse.builder()
                        .productId(102L)
                        .productName("Another Product")
                        .unitPrice(100.0)
                        .quantity(2)
                        .discountPercent(0.0)
                        .lineTotal(200.0)
                        .build()))
                .build();

        when(orderService.findByCustomerId(42L)).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/api/orders/customer/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].items[0].productId").value(101))
                .andExpect(jsonPath("$[0].items[0].productName").value("Test Product"))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].items[0].productId").value(102))
                .andExpect(jsonPath("$[1].items[0].productName").value("Another Product"));
    }
}
