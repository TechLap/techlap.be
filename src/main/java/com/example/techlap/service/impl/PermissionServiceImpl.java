package com.example.techlap.service.impl;

import com.example.techlap.domain.Permission;
import com.example.techlap.domain.QPermission;
import com.example.techlap.domain.Role;
import com.example.techlap.domain.criteria.CriteriaFilterPermission;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResPermissionDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.PermissionRepository;
import com.example.techlap.repository.RoleRepository;
import com.example.techlap.service.PermissionService;
import com.querydsl.core.BooleanBuilder;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Name already exists";
    private static final String PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE = "Permission not found";

    private Permission findPermissionByIdOrThrow(long id) {
        return this.permissionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PERMISSION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public ResPermissionDTO convertToResPermissionDTO(Permission permission) {
        return modelMapper.map(permission, ResPermissionDTO.class);
    }

    @Override
    public Permission create(Permission permission) throws Exception {
        // Check Permissionname
        if (this.permissionRepository.existsByName(permission.getName()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);

        return permissionRepository.save(permission);
    }

    @Override
    public Permission update(Permission permission) throws Exception {
        Permission permissionInDB = this.findPermissionByIdOrThrow(permission.getId());

        permissionInDB.setName(permission.getName());
        permissionInDB.setApiPath(permission.getApiPath());
        permissionInDB.setMethod(permission.getMethod());
        if (permission.getRoles() != null) {
            List<Long> roleIds = permission.getRoles().stream().map(Role::getId).toList();
            List<Role> roles = this.roleRepository.findByIdIn(roleIds);
            permissionInDB.setRoles(roles);
        }

        return this.permissionRepository.save(permissionInDB);
    }

    @Override
    public Permission fetchPermissionById(long id) throws Exception {
        return this.findPermissionByIdOrThrow(id);
    }

    @Override
    public Permission fetchPermissionByName(String name) {
        return this.permissionRepository
                .findByName(name);
    }

    @Override
    public void delete(long id) throws Exception {
        Permission permission = this.findPermissionByIdOrThrow(id);
        this.permissionRepository.delete(permission);
    }

    @Override
    public ResPaginationDTO fetchAllPermissionsWithPagination(Pageable pageable) throws Exception {
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(permissionPage.getNumber() + 1);
        meta.setPageSize(permissionPage.getSize());
        meta.setPages(permissionPage.getTotalPages());
        meta.setTotal(permissionPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(permissionPage.getContent());

        List<ResPermissionDTO> listPermissions = permissionPage.getContent()
                .stream().map(item -> this.convertToResPermissionDTO(item))
                .collect(Collectors.toList());
        res.setResult(listPermissions);

        return res;
    }

    @Override
    public ResPaginationDTO filterPermissions(Pageable pageable, CriteriaFilterPermission criteriaFilterPermission)
            throws Exception {
        QPermission qPermission = QPermission.permission;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteriaFilterPermission.getName() != null && !criteriaFilterPermission.getName().isEmpty()) {
            builder.and(qPermission.name.containsIgnoreCase(criteriaFilterPermission.getName()));

        }
        if (criteriaFilterPermission.getApiPath() != null && !criteriaFilterPermission.getApiPath().isEmpty()) {
            builder.and(qPermission.apiPath.containsIgnoreCase(criteriaFilterPermission.getApiPath()));
        }
        if (criteriaFilterPermission.getMethod() != null && !criteriaFilterPermission.getMethod().isEmpty()) {
            builder.and(qPermission.method.containsIgnoreCase(criteriaFilterPermission.getMethod()));
        }
        if (criteriaFilterPermission.getModule() != null && !criteriaFilterPermission.getModule().isEmpty()) {
            builder.and(qPermission.module.containsIgnoreCase(criteriaFilterPermission.getModule()));
        }

        if (criteriaFilterPermission.getCreatedAt() != null && !criteriaFilterPermission.getCreatedAt().isEmpty()) {
            LocalDate locateDate = LocalDate.parse(criteriaFilterPermission.getCreatedAt());
            ZoneId zone = ZoneId.systemDefault();
            Instant from = locateDate.atStartOfDay(zone).toInstant();
            Instant to = locateDate.plusDays(1).atStartOfDay(zone).toInstant();
            builder.and(qPermission.createdAt.between(from, to));
        }

        Page<Permission> permissionPage = permissionRepository.findAll(builder, pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        meta.setPage(permissionPage.getNumber() + 1);
        meta.setPageSize(permissionPage.getSize());
        meta.setPages(permissionPage.getTotalPages());
        meta.setTotal(permissionPage.getTotalElements());
        res.setMeta(meta);

        List<ResPermissionDTO> listPermissions = permissionPage.getContent()
                .stream().map(item -> this.convertToResPermissionDTO(item))
                .collect(Collectors.toList());
        res.setResult(listPermissions);
        return res;
    }

}
