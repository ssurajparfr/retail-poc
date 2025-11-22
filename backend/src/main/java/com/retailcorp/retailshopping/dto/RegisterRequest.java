package com.retailcorp.retailshopping.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank private String firstName;
    @NotBlank private String lastName;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password; // plain-text coming from frontend

    @NotBlank private String phone;

    @NotBlank private String address;
    @NotBlank private String city;
    @NotBlank private String state;
    @NotBlank private String zipCode;
    @NotBlank private String country;

    private String customerSegment;
}
