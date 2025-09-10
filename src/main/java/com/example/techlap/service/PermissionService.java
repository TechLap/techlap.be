package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Permission;
import com.example.techlap.domain.criteria.CriteriaFilterPermission;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResPermissionDTO;

public interface PermissionService {
    // Create a permission
    Permission create(Permission permission) throws Exception;

    // Update a permission
    Permission update(Permission permission) throws Exception;

    // Find a permission by id
    Permission fetchPermissionById(long id) throws Exception;

    // Find a permission by permissionname
    Permission fetchPermissionByName(String name);

    // Find all permission with pagination
    ResPaginationDTO fetchAllPermissionsWithPagination(Pageable pageable) throws Exception;

    // Delete a permission by id
    void delete(long id) throws Exception;

    ResPermissionDTO convertToResPermissionDTO(Permission permission) throws Exception;

    ResPaginationDTO filterPermissions(Pageable pageable, CriteriaFilterPermission criteriaFilterPermission)
            throws Exception;

}
