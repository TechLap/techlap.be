package com.example.techlap.repository;

import com.example.techlap.domain.User;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmail(@NotBlank(message = "email isn't blank") String email);

}
