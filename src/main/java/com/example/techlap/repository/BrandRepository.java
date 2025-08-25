package com.example.techlap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.techlap.domain.Brand;


public interface BrandRepository extends JpaRepository<Brand, Long> {

    Brand findByName(String name);
}
