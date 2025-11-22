package com.retailcorp.retailshopping.unit.service;

import com.retailcorp.retailshopping.dto.CustomerEventRequest;
import com.retailcorp.retailshopping.dto.CustomerEventResponse;
import com.retailcorp.retailshopping.entity.CustomerEvent;
import com.retailcorp.retailshopping.repository.CustomerEventRepository;
import com.retailcorp.retailshopping.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private CustomerEventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private CustomerEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new CustomerEvent();
        sampleEvent.setEventId(1L);
        sampleEvent.setCustomerId(100L);
        sampleEvent.setEventTimestamp(LocalDateTime.now());
        sampleEvent.setLoadTimestamp(LocalDateTime.now());
    }

    @Test
    void logEvent_savesAndReturnsResponse() {
        CustomerEventRequest request = new CustomerEventRequest();
        request.setCustomerId(100L);
        request.setEventData(Map.of("action", "login"));

        when(eventRepository.save(any(CustomerEvent.class))).thenAnswer(inv -> {
            CustomerEvent e = inv.getArgument(0);
            e.setEventId(1L);
            return e;
        });

        CustomerEventResponse response = eventService.logEvent(request);

        assertThat(response.getCustomerId()).isEqualTo(100L);
        assertThat(response.getEventData()).containsEntry("action", "login");
    }

    @Test
    void getEventsForCustomer_returnsListOfResponses() {
        sampleEvent.setEventData("{\"action\": \"purchase\"}");
        when(eventRepository.findByCustomerIdOrderByEventTimestampDesc(100L))
                .thenReturn(List.of(sampleEvent));

        List<CustomerEventResponse> responses = eventService.getEventsForCustomer(100L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getEventData()).containsEntry("action", "purchase");
    }

    @Test
    void jsonToMap_returnsEmptyMapForNullJson() {
        sampleEvent.setEventData(null);
        when(eventRepository.findByCustomerIdOrderByEventTimestampDesc(100L))
                .thenReturn(List.of(sampleEvent));

        List<CustomerEventResponse> responses = eventService.getEventsForCustomer(100L);

        assertThat(responses.get(0).getEventData()).isEmpty();
    }

    @Test
    void jsonToMap_returnsEmptyMapForBlankJson() {
        sampleEvent.setEventData("   ");
        when(eventRepository.findByCustomerIdOrderByEventTimestampDesc(100L))
                .thenReturn(List.of(sampleEvent));

        List<CustomerEventResponse> responses = eventService.getEventsForCustomer(100L);

        assertThat(responses.get(0).getEventData()).isEmpty();
    }

    @Test
    void jsonToMap_throwsRuntimeExceptionForInvalidJson() {
        sampleEvent.setEventData("not valid json {{{");
        when(eventRepository.findByCustomerIdOrderByEventTimestampDesc(100L))
                .thenReturn(List.of(sampleEvent));

        assertThatThrownBy(() -> eventService.getEventsForCustomer(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid JSON stored in event_data");
    }

    @Test
    void mapToJson_throwsRuntimeExceptionForUnserializableData() {
        CustomerEventRequest request = new CustomerEventRequest();
        request.setCustomerId(100L);
        
        // Create a map with a value that can't be serialized
        Map<String, Object> badData = Map.of("bad", new Object() {
            // Anonymous class without proper serialization
        });
        request.setEventData(badData);

        assertThatThrownBy(() -> eventService.logEvent(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to serialize event_data");
    }
}