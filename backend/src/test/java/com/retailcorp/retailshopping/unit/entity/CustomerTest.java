package com.retailcorp.retailshopping.unit.entity;

import com.retailcorp.retailshopping.entity.Customer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    // Test subclass to access protected method
    static class TestableCustomer extends Customer {
        public void callOnCreate() {
            onCreate();
        }
    }

    @Test
    void onCreate_setsRegistrationDateWhenNull() {
        TestableCustomer customer = new TestableCustomer();
        customer.setFirstName("John");
        customer.setEmail("john@example.com");
        
        customer.callOnCreate();
        
        assertThat(customer.getRegistrationDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void onCreate_doesNotOverrideExistingRegistrationDate() {
        TestableCustomer customer = new TestableCustomer();
        LocalDate existingDate = LocalDate.of(2023, 1, 15);
        customer.setRegistrationDate(existingDate);
        
        customer.callOnCreate();
        
        assertThat(customer.getRegistrationDate()).isEqualTo(existingDate);
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        LocalDate regDate = LocalDate.of(2024, 6, 1);
        
        Customer customer = new Customer(
            1L, "John", "Doe", "john@example.com", "hashedpwd",
            "1234567890", "123 Main St", "New York", "NY", "10001", "USA",
            regDate, "Premium", 1500.0
        );
        
        assertThat(customer.getCustomerId()).isEqualTo(1L);
        assertThat(customer.getFirstName()).isEqualTo("John");
        assertThat(customer.getLastName()).isEqualTo("Doe");
        assertThat(customer.getEmail()).isEqualTo("john@example.com");
        assertThat(customer.getPasswordHash()).isEqualTo("hashedpwd");
        assertThat(customer.getPhone()).isEqualTo("1234567890");
        assertThat(customer.getAddress()).isEqualTo("123 Main St");
        assertThat(customer.getCity()).isEqualTo("New York");
        assertThat(customer.getState()).isEqualTo("NY");
        assertThat(customer.getZipCode()).isEqualTo("10001");
        assertThat(customer.getCountry()).isEqualTo("USA");
        assertThat(customer.getRegistrationDate()).isEqualTo(regDate);
        assertThat(customer.getCustomerSegment()).isEqualTo("Premium");
        assertThat(customer.getLifetimeValue()).isEqualTo(1500.0);
    }

    @Test
    void noArgsConstructor_createsEmptyCustomer() {
        Customer customer = new Customer();
        
        assertThat(customer.getCustomerId()).isNull();
        assertThat(customer.getFirstName()).isNull();
        assertThat(customer.getEmail()).isNull();
    }
}