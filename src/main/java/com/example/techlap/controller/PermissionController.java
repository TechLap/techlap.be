package com.example.techlap.controller;

import com.example.techlap.domain.Permission;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;


    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws Exception {
        Permission newPermission = permissionService.create(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws Exception {
        Permission currentPermission = permissionService.update(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(currentPermission);
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Fetch permission by id")
    public ResponseEntity<Permission> fetchPermissionById(@PathVariable("id") long id) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.fetchPermissionById(id));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission by id")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws Exception {
        this.permissionService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResPaginationDTO> fetchAllPermissions(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.permissionService.fetchAllPermissionsWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

}
