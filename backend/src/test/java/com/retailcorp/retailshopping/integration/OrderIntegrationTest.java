package com.retailcorp.retailshopping.integration;

import com.jayway.jsonpath.JsonPath;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.entity.Product;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderIntegrationTest extends BaseIntegrationTest {

    String token;
    Long customerId;

        @Autowired
private CustomerRepository customerRepository;

@Autowired
private ProductRepository productRepository;

@Autowired
private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
    customerRepository.deleteAll();
    productRepository.deleteAll();

    // Seed product
    Product product = new Product();
    product.setProductId(101L);
    product.setProductName("Laptop Pro 15");
    product.setCategory("Electronics");
    product.setSubcategory("Computers");
    product.setBrand("TechBrand");
    product.setUnitPrice(1299.99);
    product.setCostPrice(900.0);
    product.setStockQuantity(50);
    product.setReorderLevel(10);
    product.setIsActive(true);                    // mandatory
    product.setCreatedDate(LocalDate.now());      // mandatory
    product.setLastUpdated(LocalDateTime.now());  // optional
    productRepository.save(product);

        // Create test user
    Customer customer = new Customer();
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("john.doe@example.com");
    customer.setPasswordHash(passwordEncoder.encode("Password123"));
    customer.setPhone("1234567890");
    customer.setRegistrationDate(LocalDate.now());
    customer.setLifetimeValue(0.0);
    customerRepository.save(customer);
    customerId = customer.getCustomerId();

        String loginBody = """
            {"email": "john.doe@example.com", "password": "Password123"}
        """;

        ResponseEntity<String> login = rest.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(loginBody, jsonHeaders()),
                String.class
        );

        token = JsonPath.read(login.getBody(), "$.token");



    }

    @Test
    void testCheckout() {
        String body = """
        {
            "customerId": %d,
            "paymentMethod": "Credit Card",
            "shippingAddress": "123 Main St",
            "items": [
                {"productId": 101, "quantity": 1}
            ]
        }
        """.formatted(customerId);

        ResponseEntity<String> res = rest.postForEntity(
                "/api/orders/checkout",
                new HttpEntity<>(body, authHeaders(token)),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("orderId");
    }

    @Test
void testGetOrdersByCustomer() {
    // First login and get customerId
    String loginBody = """
        { "email": "john.doe@example.com", "password": "Password123" }
    """;

    ResponseEntity<String> loginRes = rest.postForEntity(
            "/api/auth/login",
            new HttpEntity<>(loginBody, jsonHeaders()),
            String.class
    );

    String token = JsonPath.read(loginRes.getBody(), "$.token");
    
    ResponseEntity<String> meRes = rest.exchange(
            "/api/auth/me",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(token)),
            String.class
    );
    
    Integer customerId = JsonPath.read(meRes.getBody(), "$.customerId");

    ResponseEntity<String> res = rest.exchange(
            "/api/orders/customer/" + customerId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(token)),
            String.class
    );

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
}
}
