package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Role;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;

public interface RoleService {
    // Create a role
    Role create(Role role) throws Exception;

    // Update a role
    Role update(Role role) throws Exception;

    // Find a role by id
    Role fetchRoleById(long id) throws Exception;

    // Find a role by rolename
    Role fetchRoleByName(String name);

    // Find all role with pagination
    ResPaginationDTO fetchAllRolesWithPagination(Pageable pageable) throws Exception;

    // Delete a role by id
    void delete(long id) throws Exception;

}
