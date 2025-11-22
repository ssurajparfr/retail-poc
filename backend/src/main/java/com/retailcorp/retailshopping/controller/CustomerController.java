package com.retailcorp.retailshopping.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.retailcorp.retailshopping.dto.CustomerEventResponse;
import com.retailcorp.retailshopping.dto.CustomerResponse;
import com.retailcorp.retailshopping.dto.RegisterRequest;
import com.retailcorp.retailshopping.service.CustomerService;
import com.retailcorp.retailshopping.service.EventService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@Valid @RequestBody RegisterRequest request) {
        return customerService.createCustomer(request);
    }

    @GetMapping
    public List<CustomerResponse> searchCustomers(@RequestParam(required = false) String email) {
        return customerService.searchByEmail(email);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/{id}/events")
    public List<CustomerEventResponse> getCustomerEvents(@PathVariable Long id) {
        return eventService.getEventsForCustomer(id);
    }
}
