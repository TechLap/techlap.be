package com.example.techlap.repository;

import com.example.techlap.domain.Permission;

import jakarta.validation.constraints.NotBlank;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(String name);

    Permission findByName(@NotBlank(message = "name isn't blank") String name);
}
