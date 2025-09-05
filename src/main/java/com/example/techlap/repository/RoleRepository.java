package com.example.techlap.repository;

import com.example.techlap.domain.Role;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String name);

    Role findByName(@NotBlank(message = "name isn't blank") String name);

    java.util.List<Role> findByIdIn(List<Long> ids);
}
