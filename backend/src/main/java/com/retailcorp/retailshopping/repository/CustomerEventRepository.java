package com.retailcorp.retailshopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retailcorp.retailshopping.entity.CustomerEvent;

@Repository
public interface CustomerEventRepository extends JpaRepository<CustomerEvent, Long> {

    List<CustomerEvent> findByCustomerIdOrderByEventTimestampDesc(Long customerId);}