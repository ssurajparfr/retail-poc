package com.retailcorp.retailshopping.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerIntegrationTest extends BaseIntegrationTest {

    String token;
    Integer customerId;

    @BeforeEach
    void setup() {
        // Login (assumes the user already exists from previous test)
        String loginBody = """
            { "email": "john.doe@example.com", "password": "Password123" }
        """;

        ResponseEntity<String> loginRes = rest.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(loginBody, jsonHeaders()),
                String.class
        );

        token = JsonPath.read(loginRes.getBody(), "$.token");

        // /me for customer ID
        ResponseEntity<String> meRes = rest.exchange(
                "/api/auth/me",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class
        );

        customerId = JsonPath.read(meRes.getBody(), "$.customerId");
    }

    @Test
    void testGetCustomerById() {
        ResponseEntity<String> res = rest.exchange(
                "/api/customers/" + customerId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("john.doe@example.com");
    }

    @Test
    void testSearchCustomersByEmail() {
        ResponseEntity<String> res = rest.exchange(
                "/api/customers?email=john.doe@example.com",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("john.doe@example.com");
    }

    @Test
    void testCreateCustomer() {
        String createBody = """
            {
              "firstName": "Jane",
              "lastName": "Smith",
              "email": "jane.smith@example.com",
              "password": "Password456",
              "phone": "9876543210",
              "address": "456 Oak Ave",
              "city": "Boston",
              "state": "MA",
              "zipCode": "02101",
              "country": "USA"
            }
        """;

        ResponseEntity<String> res = rest.postForEntity(
                "/api/customers",
                new HttpEntity<>(createBody, jsonHeaders()),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).contains("jane.smith@example.com");
    }

    @Test
    void testGetCustomerEvents() {
        ResponseEntity<String> res = rest.exchange(
                "/api/customers/" + customerId + "/events",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}