package com.retailcorp.retailshopping.service;

import com.retailcorp.retailshopping.dto.*;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class AuthService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new customer
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setZipCode(request.getZipCode());
        customer.setCountry(request.getCountry());
        customer.setRegistrationDate(LocalDate.now());
        customer.setCustomerSegment("Standard");
        customer.setLifetimeValue(0.0);
        
        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        
        // Generate JWT token
        String token = tokenProvider.generateToken(savedCustomer.getEmail());
        
        return new AuthResponse(token, savedCustomer);
    }
    
    public AuthResponse login(LoginRequest request) {
        // Find customer by email
        Customer customer = customerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Generate JWT token
        String token = tokenProvider.generateToken(customer.getEmail());
        
        return new AuthResponse(token, customer);
    }
    
    public Customer getCurrentCustomer(String email) {
        return customerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
