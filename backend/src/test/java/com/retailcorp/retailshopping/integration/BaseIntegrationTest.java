package com.retailcorp.retailshopping.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import com.retailcorp.retailshopping.unit.config.TestSecurityConfig;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false) 
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate rest;
    
    protected HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    protected HttpHeaders authHeaders(String token) {
        HttpHeaders h = jsonHeaders();
        h.set("Authorization", "Bearer " + token);
        return h;
    }
}
