package com.example.techlap.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.techlap.domain.Role;
import com.example.techlap.domain.User;
import com.example.techlap.domain.request.ReqUpdateUserDTO;
import com.example.techlap.domain.respond.DTO.ResCreateUserDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResUpdateUserDTO;
import com.example.techlap.domain.respond.DTO.ResUserDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.UserRepository;
import com.example.techlap.service.RoleService;
import com.example.techlap.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found";

    private User findUserByIdOrThrow(long id) {
        return this.userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public User create(User user) throws Exception {
        // Check Username
        if (this.userRepository.existsByEmail(user.getEmail()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);

        // Save hashPassword
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Check role
        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }

        return userRepository.save(user);
    }

    @Override
    public User update(ReqUpdateUserDTO reqUser) throws Exception {
        User userInDB = this.findUserByIdOrThrow(reqUser.getId());

        userInDB.setFullName(reqUser.getFullName());
        userInDB.setPhone(reqUser.getPhone());
        userInDB.setAddress(reqUser.getAddress());

        return this.userRepository.save(userInDB);
    }

    @Override
    public User fetchUserById(long id) throws Exception {
        return this.findUserByIdOrThrow(id);
    }

    @Override
    public User fetchUserByEmail(String email) {
        return this.userRepository
                .findByEmail(email);
    }

    @Override
    public void delete(long id) throws Exception {
        User user = this.findUserByIdOrThrow(id);
        this.userRepository.delete(user);
    }

    @Override
    public ResPaginationDTO fetchAllUsersWithPagination(Pageable pageable) throws Exception {
        Page<User> userPage = userRepository.findAll(pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(userPage.getNumber() + 1);
        meta.setPageSize(userPage.getSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(userPage.getContent());

        // remove sentitive data
        List<ResUserDTO> listUser = userPage.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        res.setResult(listUser);

        return res;
    }

    @Override
    public void updateUserToken(String token, String email) throws Exception {
        User currentUser = this.fetchUserByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    @Override
    public User getUserByRefreshTokenAndEmail(String token, String email) throws Exception {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);

    }

    public ResUserDTO convertToResUserDTO(User user) {
        return modelMapper.map(user, ResUserDTO.class);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        return modelMapper.map(user, ResCreateUserDTO.class);
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        return modelMapper.map(user, ResUpdateUserDTO.class);
    }

}
