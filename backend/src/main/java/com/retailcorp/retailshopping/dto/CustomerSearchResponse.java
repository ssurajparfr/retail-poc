package com.retailcorp.retailshopping.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerSearchResponse {
    private Long id;
    private String fullName;
    private String email;
}
