package com.retailcorp.retailshopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retailcorp.retailshopping.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        List<Product> findByProductNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String category, String brand);

}