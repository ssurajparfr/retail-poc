package com.retailcorp.retailshopping.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.retailcorp.retailshopping.dto.CustomerEventRequest;
import com.retailcorp.retailshopping.dto.CustomerEventResponse;
import com.retailcorp.retailshopping.service.EventService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public CustomerEventResponse logEvent(@Valid @RequestBody CustomerEventRequest request) {
        return eventService.logEvent(request);
    }

    @GetMapping("/customer/{customerId}")
    public List<CustomerEventResponse> getEventsForCustomer(@PathVariable Long customerId) {
        return eventService.getEventsForCustomer(customerId);
    }
}
