package com.retailcorp.retailshopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(
title = "My API",
version = "1.0",
description = "API documentation for My Application"
))
@SpringBootApplication
public class SnowflakePocApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnowflakePocApplication.class, args);
	}

}
