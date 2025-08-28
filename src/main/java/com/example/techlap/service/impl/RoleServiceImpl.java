package com.example.techlap.service.impl;

import com.example.techlap.domain.Role;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.RoleRepository;
import com.example.techlap.service.RoleService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Name already exists";
    private static final String ROLE_NOT_FOUND_EXCEPTION_MESSAGE = "Role not found";

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private Role findRoleByIdOrThrow(long id) {
        return this.roleRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public Role create(Role role) throws Exception {
        // Check Rolename
        if (this.roleRepository.existsByName(role.getName()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);

        return roleRepository.save(role);
    }

    @Override
    public Role update(Role role) throws Exception {
        Role roleInDB = this.findRoleByIdOrThrow(role.getId());

        roleInDB.setName(role.getName());
        roleInDB.setDescription(role.getDescription());

        return this.roleRepository.save(roleInDB);
    }

    @Override
    public Role fetchRoleById(long id) throws Exception {
        return this.findRoleByIdOrThrow(id);
    }

    @Override
    public Role fetchRoleByName(String name) {
        return this.roleRepository
                .findByName(name);
    }

    @Override
    public void delete(long id) throws Exception {
        Role role = this.findRoleByIdOrThrow(id);
        this.roleRepository.delete(role);
    }

    @Override
    public ResPaginationDTO fetchAllRolesWithPagination(Pageable pageable) throws Exception {
        Page<Role> rolePage = roleRepository.findAll(pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(rolePage.getNumber() + 1);
        meta.setPageSize(rolePage.getSize());
        meta.setPages(rolePage.getTotalPages());
        meta.setTotal(rolePage.getTotalElements());

        res.setMeta(meta);
        res.setResult(rolePage.getContent());

        return res;
    }
}
