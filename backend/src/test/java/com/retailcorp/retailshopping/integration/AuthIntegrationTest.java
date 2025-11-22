package com.retailcorp.retailshopping.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import com.retailcorp.retailshopping.repository.CustomerRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
private CustomerRepository customerRepository;

    @BeforeEach
    void setup() {
        customerRepository.deleteAll();
        // seed other necessary data
    }

    @Test
    void testRegisterAndLogin() {
        // Register
        String registerBody = """
            {
              "firstName": "John",
              "lastName": "Doe",
              "email": "john.doe@example.com",
              "password": "Password123",
              "phone": "1234567890",
              "address": "123 Main St",
              "city": "New York",
              "state": "NY",
              "zipCode": "10001",
              "country": "USA",
              "customerSegment": "Standard"
            }
        """;

        ResponseEntity<String> regRes = rest.postForEntity(
                "/api/auth/register",
                new HttpEntity<>(registerBody, jsonHeaders()),
                String.class
        );

        assertThat(regRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Login
        String loginBody = """
            { "email": "john.doe@example.com", "password": "Password123" }
        """;

        ResponseEntity<String> loginRes = rest.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(loginBody, jsonHeaders()),
                String.class
        );

        assertThat(loginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginRes.getBody()).contains("token");
   
        
        ResponseEntity<String> res = rest.exchange(
                "/api/auth/me",
                HttpMethod.GET,
                new HttpEntity<>(jsonHeaders()),
                String.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody()).contains("Missing or invalid Authorization header");
    }

    
}

