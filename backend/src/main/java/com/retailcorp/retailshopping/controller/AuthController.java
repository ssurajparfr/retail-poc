package com.retailcorp.retailshopping.controller;

import com.retailcorp.retailshopping.dto.*;
import com.retailcorp.retailshopping.service.AuthService;
import com.retailcorp.retailshopping.service.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentCustomer(@RequestHeader(value="Authorization", required=false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7); // remove "Bearer "
            String email = tokenProvider.getEmailFromToken(token);
            CustomerResponse customer = authService.getCurrentCustomer(email);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new ErrorResponse(e.getMessage()));
        }
    }
}
