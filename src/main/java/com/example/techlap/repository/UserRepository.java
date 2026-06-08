package com.example.techlap.repository;

import com.example.techlap.domain.User;
import jakarta.validation.constraints.NotBlank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    boolean existsByEmail(String email);

    User findByEmail(@NotBlank(message = "email isn't blank") String email);

    User findByRefreshTokenAndEmail(String refreshToken, String email);

}
