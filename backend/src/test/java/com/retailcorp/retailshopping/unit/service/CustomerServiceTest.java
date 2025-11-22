package com.retailcorp.retailshopping.unit.service;

import com.retailcorp.retailshopping.dto.CustomerResponse;
import com.retailcorp.retailshopping.dto.RegisterRequest;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.exception.ResourceNotFoundException;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_success_returnsCustomerResponse() {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john.doe@example.com");
        req.setPassword("Password123");
        req.setPhone("1234567890");
        req.setAddress("123 Main St");
        req.setCity("City");
        req.setState("State");
        req.setZipCode("12345");
        req.setCountry("Country");

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(1L);
        savedCustomer.setFirstName(req.getFirstName());
        savedCustomer.setLastName(req.getLastName());
        savedCustomer.setEmail(req.getEmail());
        savedCustomer.setPasswordHash("hashedPassword");
        savedCustomer.setPhone(req.getPhone());
        savedCustomer.setAddress(req.getAddress());
        savedCustomer.setCity(req.getCity());
        savedCustomer.setState(req.getState());
        savedCustomer.setZipCode(req.getZipCode());
        savedCustomer.setCountry(req.getCountry());
        savedCustomer.setCustomerSegment("Standard");
        savedCustomer.setLifetimeValue(0.0);
        savedCustomer.setRegistrationDate(LocalDate.now());

        when(passwordEncoder.encode(req.getPassword())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse response = customerService.createCustomer(req);

        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");

        verify(passwordEncoder).encode("Password123");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void getCustomerById_existingCustomer_returnsCustomerResponse() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setFirstName("Jane");
        customer.setEmail("jane@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getCustomerById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void getCustomerById_nonExistingCustomer_throwsException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void searchByEmail_existingCustomer_returnsList() {
        Customer customer = new Customer();
        customer.setCustomerId(2L);
        customer.setEmail("search@example.com");

        when(customerRepository.findByEmail("search@example.com"))
                .thenReturn(Optional.of(customer));

        List<CustomerResponse> result = customerService.searchByEmail("search@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(2L);
        assertThat(result.get(0).getEmail()).isEqualTo("search@example.com");
    }

    @Test
    void searchByEmail_nonExistingCustomer_returnsEmptyList() {
        when(customerRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        List<CustomerResponse> result = customerService.searchByEmail("notfound@example.com");

        assertThat(result).isEmpty();
    }

    @Test
void createCustomer_withCustomerSegment_usesProvidedSegment() {
    RegisterRequest req = new RegisterRequest();
    req.setFirstName("Jane");
    req.setLastName("Smith");
    req.setEmail("jane.smith@example.com");
    req.setPassword("Password456");
    req.setPhone("9876543210");
    req.setAddress("456 Oak Ave");
    req.setCity("Boston");
    req.setState("MA");
    req.setZipCode("02101");
    req.setCountry("USA");
    req.setCustomerSegment("Premium");  // Explicitly set segment

    Customer savedCustomer = new Customer();
    savedCustomer.setCustomerId(2L);
    savedCustomer.setFirstName(req.getFirstName());
    savedCustomer.setLastName(req.getLastName());
    savedCustomer.setEmail(req.getEmail());
    savedCustomer.setPasswordHash("hashedPassword");
    savedCustomer.setCustomerSegment("Premium");
    savedCustomer.setLifetimeValue(0.0);
    savedCustomer.setRegistrationDate(LocalDate.now());

    when(passwordEncoder.encode(req.getPassword())).thenReturn("hashedPassword");
    when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

    CustomerResponse response = customerService.createCustomer(req);

    assertThat(response).isNotNull();
    assertThat(response.getCustomerSegment()).isEqualTo("Premium");
}
}