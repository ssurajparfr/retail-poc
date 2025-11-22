package com.retailcorp.retailshopping.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.retailcorp.retailshopping.dto.OrderRequest;
import com.retailcorp.retailshopping.dto.OrderResponse;
import com.retailcorp.retailshopping.service.OrderService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public OrderResponse checkout(@Valid @RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.findByCustomerId(customerId);
    }
}
