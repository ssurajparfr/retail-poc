package com.retailcorp.retailshopping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailcorp.retailshopping.dto.CustomerEventRequest;
import com.retailcorp.retailshopping.dto.CustomerEventResponse;
import com.retailcorp.retailshopping.entity.CustomerEvent;
import com.retailcorp.retailshopping.repository.CustomerEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final CustomerEventRepository eventRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public CustomerEventResponse logEvent(CustomerEventRequest req) {
        CustomerEvent event = toEntity(req);
        CustomerEvent saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public List<CustomerEventResponse> getEventsForCustomer(Long customerId) {
        return eventRepository.findByCustomerIdOrderByEventTimestampDesc(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private CustomerEventResponse toResponse(CustomerEvent e) {
        return CustomerEventResponse.builder()
                .eventId(e.getEventId())
                .customerId(e.getCustomerId())
                .eventTimestamp(e.getEventTimestamp())
                .eventData(jsonToMap(e.getEventData()))
                .loadTimestamp(e.getLoadTimestamp())
                .build();
    }

    private CustomerEvent toEntity(CustomerEventRequest req) {
        CustomerEvent event = new CustomerEvent();
        event.setCustomerId(req.getCustomerId());
        event.setEventTimestamp(LocalDateTime.now());
        event.setEventData(mapToJson(req.getEventData()));
        event.setLoadTimestamp(LocalDateTime.now());
        return event;
    }

    private Map<String, Object> jsonToMap(String json) {
        try {
            if (json == null || json.isBlank()) {
                return Map.of();
            }
            return mapper.readValue(json, new TypeReference<Map<String,Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON stored in event_data", e);
        }
    }

    private String mapToJson(Map<String, Object> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event_data", e);
        }
    }
}
