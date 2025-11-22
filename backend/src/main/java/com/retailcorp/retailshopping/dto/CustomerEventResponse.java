package com.retailcorp.retailshopping.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerEventResponse {
    private Long eventId;
    private Long customerId;
    private LocalDateTime eventTimestamp;
    private Map<String, Object> eventData;
    private LocalDateTime loadTimestamp;
}
