package com.retailcorp.retailshopping.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;

    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    private String customerSegment;
    private Double lifetimeValue;

    private LocalDate registrationDate;
}
