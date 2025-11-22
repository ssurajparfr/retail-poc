package com.retailcorp.retailshopping.unit.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Collections;
import java.util.List;

import com.retailcorp.retailshopping.controller.CustomerController;
import com.retailcorp.retailshopping.dto.CustomerEventResponse;
import com.retailcorp.retailshopping.dto.CustomerResponse;
import com.retailcorp.retailshopping.dto.RegisterRequest;
import com.retailcorp.retailshopping.service.CustomerService;
import com.retailcorp.retailshopping.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;





public class CustomerControllerTest {

    private MockMvc mockMvc;
    private CustomerService customerService;
    private EventService eventService;

    @BeforeEach
    public void setup() {
        customerService = Mockito.mock(CustomerService.class);
        eventService = Mockito.mock(EventService.class);
        CustomerController controller = new CustomerController(customerService, eventService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void createCustomer_returnsCreated_andDelegatesToService() throws Exception {
        CustomerResponse mockedResponse = Mockito.mock(CustomerResponse.class);
        when(customerService.createCustomer(any(RegisterRequest.class))).thenReturn(mockedResponse);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(customerService).createCustomer(any(RegisterRequest.class));
    }

    @Test
    public void searchCustomers_withoutEmail_callsServiceAndReturnsOk() throws Exception {
        when(customerService.searchByEmail(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/customers"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(customerService).searchByEmail(null);
    }

    @Test
    public void searchCustomers_withEmail_callsServiceAndReturnsOk() throws Exception {
        String email = "user@example.com";
        when(customerService.searchByEmail(email)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/customers").param("email", email))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(customerService).searchByEmail(email);
    }

    @Test
    public void getCustomer_callsServiceAndReturnsOk() throws Exception {
        Long id = 42L;
        CustomerResponse mockedResponse = Mockito.mock(CustomerResponse.class);
        when(customerService.getCustomerById(id)).thenReturn(mockedResponse);

        mockMvc.perform(get("/api/customers/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(customerService).getCustomerById(id);
    }

    @Test
    public void getCustomerEvents_callsEventServiceAndReturnsOk() throws Exception {
        Long id = 7L;
        List<CustomerEventResponse> empty = Collections.emptyList();
        when(eventService.getEventsForCustomer(id)).thenReturn(empty);

        mockMvc.perform(get("/api/customers/{id}/events", id))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(eventService).getEventsForCustomer(id);
    }
}