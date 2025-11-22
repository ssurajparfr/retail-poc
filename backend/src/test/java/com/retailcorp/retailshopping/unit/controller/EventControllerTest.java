package com.retailcorp.retailshopping.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcorp.retailshopping.controller.EventController;
import com.retailcorp.retailshopping.dto.CustomerEventRequest;
import com.retailcorp.retailshopping.dto.CustomerEventResponse;
import com.retailcorp.retailshopping.repository.CustomerEventRepository;
import com.retailcorp.retailshopping.service.EventService;
import com.retailcorp.retailshopping.service.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.retailcorp.retailshopping.repository.CustomerRepository customerRepository;

    // ============================
    // ============================
    // POST /api/events
    // ============================
    @Test
    void logEvent_success_returnsEventResponse() throws Exception {
        CustomerEventRequest request = new CustomerEventRequest();
        request.setCustomerId(42L);
        request.setEventData(Map.of("action", "LOGIN"));

        CustomerEventResponse response = CustomerEventResponse.builder()
                .eventId(1L)
                .customerId(42L)
                .eventTimestamp(LocalDateTime.now())
                .loadTimestamp(LocalDateTime.now())
                .eventData(Map.of("action", "LOGIN"))
                .build();

        when(eventService.logEvent(any(CustomerEventRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1))
                .andExpect(jsonPath("$.customerId").value(42))
                .andExpect(jsonPath("$.eventData.action").value("LOGIN"));
    }

    // ============================
    // GET /api/events/customer/{customerId}
    // ============================
    @Test
    void getEventsForCustomer_success_returnsList() throws Exception {
        CustomerEventResponse event1 = CustomerEventResponse.builder()
                .eventId(1L)
                .customerId(42L)
                .eventTimestamp(LocalDateTime.now())
                .loadTimestamp(LocalDateTime.now())
                .eventData(Map.of("action", "LOGIN"))
                .build();

        CustomerEventResponse event2 = CustomerEventResponse.builder()
                .eventId(2L)
                .customerId(42L)
                .eventTimestamp(LocalDateTime.now())
                .loadTimestamp(LocalDateTime.now())
                .eventData(Map.of("action", "PURCHASE"))
                .build();

        when(eventService.getEventsForCustomer(42L)).thenReturn(List.of(event1, event2));

        mockMvc.perform(get("/api/events/customer/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].eventData.action").value("LOGIN"))
                .andExpect(jsonPath("$[1].eventId").value(2))
                .andExpect(jsonPath("$[1].eventData.action").value("PURCHASE"));
    }
}
