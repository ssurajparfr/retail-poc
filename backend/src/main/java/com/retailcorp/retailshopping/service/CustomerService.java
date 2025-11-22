package com.retailcorp.retailshopping.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.retailcorp.retailshopping.dto.CustomerResponse;
import com.retailcorp.retailshopping.dto.RegisterRequest;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.exception.ResourceNotFoundException;
import com.retailcorp.retailshopping.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomerResponse createCustomer(RegisterRequest req) {

        Customer c = new Customer();

        c.setFirstName(req.getFirstName());
        c.setLastName(req.getLastName());
        c.setEmail(req.getEmail());
        c.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        c.setCity(req.getCity());
        c.setState(req.getState());
        c.setZipCode(req.getZipCode());
        c.setCountry(req.getCountry());

        c.setCustomerSegment(
            req.getCustomerSegment() != null ? req.getCustomerSegment() : "Standard"
        );

        c.setLifetimeValue(0.0);
        c.setRegistrationDate(LocalDate.now());

        Customer saved = customerRepository.save(c);
        return toResponse(saved);

}



    public CustomerResponse getCustomerById(Long id) {
        Customer customer =  customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return toResponse(customer);
    }

    // Search customer by email. Returns an empty list when none found to simplify
    // frontend fallback logic that expects an array.
    public List<CustomerResponse> searchByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customer -> List.of(toResponse(customer)))
                .orElse(List.of());
    }


    private CustomerResponse toResponse(Customer c) {
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
            .customerSegment(c.getCustomerSegment())
            .lifetimeValue(c.getLifetimeValue())
            .registrationDate(c.getRegistrationDate() != null 
                    ? c.getRegistrationDate() : null)
            .build();
}

}
