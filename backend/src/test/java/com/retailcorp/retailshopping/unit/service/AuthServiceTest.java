package com.retailcorp.retailshopping.unit.service;

import com.retailcorp.retailshopping.dto.*;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.service.AuthService;
import com.retailcorp.retailshopping.service.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success_returnsAuthResponse() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setPassword("Password123");

        when(customerRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed");
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setCustomerId(1L);
            return c;
        });
        when(tokenProvider.generateToken("test@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(req);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getCustomer().getCustomerId()).isEqualTo(1L);

        verify(customerRepository).save(any(Customer.class));
        verify(tokenProvider).generateToken("test@example.com");
    }

    @Test
    void register_existingEmail_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");

        when(customerRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");
    }

    @Test
    void login_success_returnsAuthResponse() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("Password123");

        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPasswordHash("hashed");

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("Password123", "hashed")).thenReturn(true);
        when(tokenProvider.generateToken("test@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.login(req);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_invalidPassword_throwsException() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("wrong");

        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPasswordHash("hashed");

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void getCurrentCustomer_success_returnsCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setEmail("test@example.com");

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));

        CustomerResponse response = authService.getCurrentCustomer("test@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(1L);
    }

    @Test
    void getCurrentCustomer_notFound_throwsException() {
        when(customerRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentCustomer("unknown@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Customer not found");
    }

    @Test
void login_emailNotFound_throwsException() {
    LoginRequest req = new LoginRequest();
    req.setEmail("notfound@example.com");
    req.setPassword("Password123");

    when(customerRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid email or password");
}
}
