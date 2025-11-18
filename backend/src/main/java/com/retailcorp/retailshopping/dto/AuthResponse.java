package com.retailcorp.retailshopping.dto;

import com.retailcorp.retailshopping.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Customer customer;
    
    public AuthResponse(String token, Customer customer) {
        this.token = token;
        this.customer = customer;
    }
}