package com.retailcorp.retailshopping.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retailcorp.retailshopping.entity.CustomerEvent;
import com.retailcorp.retailshopping.repository.CustomerEventRepository;

@Service
public class EventService {
    @Autowired
    private CustomerEventRepository eventRepository;

    public CustomerEvent logEvent(CustomerEvent event) {
        event.setEventTimestamp(LocalDateTime.now());
        eventRepository.save(event);
        return event;
    }

    public List<CustomerEvent> getEventsForCustomer(Long customerId) {
        return eventRepository.findByCustomerIdOrderByEventTimestampDesc(customerId);
    }
}

