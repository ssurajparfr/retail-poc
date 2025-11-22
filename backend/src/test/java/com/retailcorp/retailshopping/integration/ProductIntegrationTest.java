package com.retailcorp.retailshopping.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductIntegrationTest extends BaseIntegrationTest {

    @Test
    void testListProducts() {
        ResponseEntity<String> res = rest.getForEntity("/api/products", String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testSearchProducts() {
        ResponseEntity<String> res = rest.getForEntity("/api/products/search?query=phone", String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
