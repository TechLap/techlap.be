package com.example.techlap.controller;

import com.example.techlap.domain.Role;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResRoleDTO;
import com.example.techlap.service.RoleService;
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
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<ResRoleDTO> createRole(@Valid @RequestBody Role role) throws Exception {
        Role newRole = roleService.create(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.convertToResRoleDTO(newRole));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<ResRoleDTO> updateRole(@Valid @RequestBody Role role) throws Exception {
        Role currentRole = roleService.update(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.convertToResRoleDTO(currentRole));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<ResRoleDTO> fetchRoleById(@PathVariable("id") long id) throws Exception {
        Role role = this.roleService.fetchRoleById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.convertToResRoleDTO(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role by id")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws Exception {
        this.roleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResPaginationDTO> fetchAllRoles(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.roleService.fetchAllRolesWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

}
