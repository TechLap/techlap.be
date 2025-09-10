package com.example.techlap.repository;

import com.example.techlap.domain.Customer;
import com.mysql.cj.Query;

import jakarta.validation.constraints.NotBlank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, QuerydslPredicateExecutor<Customer> {
    boolean existsByEmail(String email);

    Customer findByEmail(@NotBlank(message = "email isn't blank") String email);

}
