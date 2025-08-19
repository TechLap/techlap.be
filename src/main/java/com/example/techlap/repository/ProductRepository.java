package com.example.techlap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.techlap.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByName(String name);
}
