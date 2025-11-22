package com.retailcorp.retailshopping.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

public class CustomerIntegrationTest extends BaseIntegrationTest {

        private static final Logger logger = LoggerFactory.getLogger(CustomerIntegrationTest.class);

    String token;
    Integer customerId;

    
    @Autowired
    private com.retailcorp.retailshopping.repository.CustomerRepository customerRepository;
    @BeforeEach
    void setup() {

        customerRepository.deleteAll();
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

        // Login request payload (make sure this user exists in your test DB)
        String loginBody = """
            { "email": "john.doe@example.com", "password": "Password123" }
        """;

        ResponseEntity<String> loginRes = rest.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(loginBody, jsonHeaders()),
                String.class
        );

        System.out.println("Login Response: " + loginRes.getBody());
        assertThat(loginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginRes.getBody()).contains("token");

        token = JsonPath.read(loginRes.getBody(), "$.token");
        assertThat(token).isNotNull();

        ResponseEntity<String> meRes = rest.exchange(
                "/api/auth/me",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class
        );

        assertThat(meRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(meRes.getBody()).contains("customerId");

        customerId = JsonPath.read(meRes.getBody(), "$.customerId");
        assertThat(customerId).isNotNull();
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void printDatasourceUrl() throws Exception {
        String url = dataSource.getConnection().getMetaData().getURL();
        logger.info("DATASOURCE URL: {}", url);
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
