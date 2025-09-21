package com.example.techlap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.example.techlap.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {

    Product findByName(String name);

    boolean existsByName(String name);

    List<Product> findTop5ByOrderByCreatedAtDesc();

    @Query(value = "SELECT p.* " +
                   "FROM products p " +
                   "LEFT JOIN order_details od ON p.id = od.product_id " +
                   "GROUP BY p.id " +
                   "ORDER BY SUM(od.quantity) DESC " +
                   "LIMIT 5",
           nativeQuery = true)
    List<Product> findTop5BestSellingProducts();
}
