package com.retailcorp.retailshopping.dto;

import java.util.Map;
import lombok.Data;

@Data
public class CustomerEventRequest {
    private Long customerId;
    private Map<String, Object> eventData;
}
