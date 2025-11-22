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
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

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

        Customer savedCustomer = customerRepository.save(customer);

        String token = tokenProvider.generateToken(savedCustomer.getEmail());

       return AuthResponse.builder()
        .token(token)
        .customer(toCustomerResponse(savedCustomer))
        .build();

    }

    public AuthResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(customer.getEmail());

       return AuthResponse.builder()
        .token(token)
        .customer(toCustomerResponse(customer))
        .build();

    }

    public CustomerResponse getCurrentCustomer(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return toCustomerResponse(customer);
    }

    private CustomerResponse toCustomerResponse(Customer c) {
        return CustomerResponse.builder()
                .customerId(c.getCustomerId())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .city(c.getCity())
                .state(c.getState())
                .zipCode(c.getZipCode())
                .country(c.getCountry())
                .registrationDate(c.getRegistrationDate())
                .customerSegment(c.getCustomerSegment())
                .lifetimeValue(c.getLifetimeValue())
                .build();
    }
}
