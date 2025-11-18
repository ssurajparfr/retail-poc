package com.retailcorp.retailshopping.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.retailcorp.retailshopping.dto.RegisterRequest;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Customer createCustomer(RegisterRequest req) {
         Customer customer = new Customer();

        customer.setFirstName(req.getFirstName());
        customer.setLastName(req.getLastName());
        customer.setEmail(req.getEmail());
        customer.setPhone(req.getPhone());
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());
        customer.setState(req.getState());
        customer.setZipCode(req.getZipCode());
        customer.setCountry(req.getCountry());

        customer.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        customer.setCustomerSegment("Standard");
        customer.setLifetimeValue(0.0);
        customer.setRegistrationDate(LocalDate.now());

        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    // Search customer by email. Returns an empty list when none found to simplify
    // frontend fallback logic that expects an array.
    public java.util.List<Customer> searchByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(java.util.List::of)
                .orElse(java.util.List.of());
    }
}
