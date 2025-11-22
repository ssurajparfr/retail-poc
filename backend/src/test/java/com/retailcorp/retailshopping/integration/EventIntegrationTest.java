package com.retailcorp.retailshopping.integration;


import com.jayway.jsonpath.JsonPath;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

public class EventIntegrationTest extends BaseIntegrationTest {

    String token;
    Integer customerId;

    @Autowired
private CustomerRepository customerRepository;

@Autowired
private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {


    customerRepository.deleteAll();
        // Create test user
    Customer customer = new Customer();
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("john.doe@example.com");
    customer.setPasswordHash(passwordEncoder.encode("Password123"));
    customer.setPhone("1234567890");
    customer.setRegistrationDate(LocalDate.now());
    customerRepository.save(customer);

        String loginBody = """
            {"email": "john.doe@example.com", "password": "Password123"}
        """;

        ResponseEntity<String> login = rest.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(loginBody, jsonHeaders()),
                String.class
        );

        token = JsonPath.read(login.getBody(), "$.token");

        ResponseEntity<String> me = rest.exchange(
                "/api/auth/me",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class
        );

        customerId = JsonPath.read(me.getBody(), "$.customerId");
    }

    @Test
    void testCreateEvent() {
        String body = """
        {
            "customerId": %d,
            "eventData": {"event_type": "wishlist_add", "productId": 101}
        }
        """.formatted(customerId);

        ResponseEntity<String> res = rest.postForEntity(
                "/api/events",
                new HttpEntity<>(body, authHeaders(token)),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
void testGetEventsForCustomer() {
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
            "/api/events/customer/" + customerId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(token)),
            String.class
    );

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
}
}
