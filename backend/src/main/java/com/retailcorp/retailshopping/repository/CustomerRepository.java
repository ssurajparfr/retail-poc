package com.retailcorp.retailshopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retailcorp.retailshopping.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {}