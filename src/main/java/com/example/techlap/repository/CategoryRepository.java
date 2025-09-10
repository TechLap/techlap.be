package com.example.techlap.repository;

import com.example.techlap.domain.Category;
import jakarta.validation.constraints.NotBlank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {
    boolean existsByName(String name);

    Category findByName(@NotBlank(message = "name isn't blank") String name);

}
