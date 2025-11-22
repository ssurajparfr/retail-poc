package com.retailcorp.retailshopping.unit.exception;

import com.retailcorp.retailshopping.exception.GlobalExceptionHandler;
import com.retailcorp.retailshopping.exception.InvalidOrderException;
import com.retailcorp.retailshopping.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returnsNotFoundStatus() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Customer not found");
        
        ResponseEntity<String> response = handler.handleNotFound(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Customer not found");
    }

    @Test
    void handleInvalidOrder_returnsBadRequestStatus() {
        InvalidOrderException ex = new InvalidOrderException("Order total cannot be negative");
        
        ResponseEntity<String> response = handler.handleInvalidOrder(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Order total cannot be negative");
    }

    @Test
    void handleGeneric_returnsInternalServerError() {
        Exception ex = new Exception("Unexpected error");
        
        ResponseEntity<String> response = handler.handleGeneric(ex);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Server error: Unexpected error");
    }
}