package com.retailcorp.retailshopping.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRegistrationRequest {

    @NotBlank private String firstName;
    @NotBlank private String lastName;

    @Email @NotBlank
    private String email;

    @NotBlank private String phone;

    @NotBlank private String address;
    @NotBlank private String city;
    @NotBlank private String state;
    @NotBlank private String zip;
    @NotBlank private String country;

    @NotBlank private String membershipType;
}
