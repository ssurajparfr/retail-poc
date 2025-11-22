package com.retailcorp.retailshopping.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcorp.retailshopping.controller.AuthController;
import com.retailcorp.retailshopping.dto.AuthResponse;
import com.retailcorp.retailshopping.dto.CustomerResponse;
import com.retailcorp.retailshopping.dto.LoginRequest;
import com.retailcorp.retailshopping.dto.RegisterRequest;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.service.AuthService;
import com.retailcorp.retailshopping.service.JwtTokenProvider;
import com.retailcorp.retailshopping.unit.config.TestSecurityConfig;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
// @AutoConfigureMockMvc(addFilters = false) // disables security filters
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerRepository customerRepository;

    // Only mock the services that AuthController depends on
    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    // ============================
    // REGISTER ENDPOINT TESTS
    // ============================

    @Test
    void register_success_returns200() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(mock(AuthResponse.class));

        String json = """
            {
                "firstName":"John",
                "lastName":"Doe",
                "email":"user@example.com",
                "password":"pass123"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_existingEmail_returns400_withMessage() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        String json = """
            {
                "firstName":"John",
                "lastName":"Doe",
                "email":"user@example.com",
                "password":"pass123"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Email already exists")));

        verify(authService).register(any(RegisterRequest.class));
    }

    // ============================
    // LOGIN ENDPOINT TESTS
    // ============================

    @Test
    void login_success_returns200() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(mock(AuthResponse.class));

        String json = """
            {
                "email":"user@example.com",
                "password":"pass"
            }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_invalidCredentials_returns401_withMessage() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        String json = """
            {
                "email":"user@example.com",
                "password":"wrong"
            }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(Matchers.containsString("Invalid credentials")));

        verify(authService).login(any(LoginRequest.class));
    }

    // ============================
    // ME ENDPOINT TESTS
    // ============================

    @Test
    void me_withValidToken_returns200() throws Exception {
        when(tokenProvider.getEmailFromToken("valid-token")).thenReturn("user@example.com");
        when(authService.getCurrentCustomer("user@example.com")).thenReturn(mock(CustomerResponse.class));

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());

        verify(tokenProvider).getEmailFromToken("valid-token");
        verify(authService).getCurrentCustomer("user@example.com");
    }

    @Test
    void me_missingAuthorizationHeader_returns401_withMessage() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(Matchers.containsString("Missing or invalid Authorization header")));

        verifyNoInteractions(tokenProvider);
        verifyNoInteractions(authService);
    }

    @Test
    void me_invalidToken_returns401_withMessage() throws Exception {
        when(tokenProvider.getEmailFromToken("bad-token"))
                .thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(Matchers.containsString("Invalid token")));

        verify(tokenProvider).getEmailFromToken("bad-token");
        verifyNoInteractions(authService);
    }

    
}
