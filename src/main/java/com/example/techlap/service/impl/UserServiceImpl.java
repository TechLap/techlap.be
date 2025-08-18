package com.example.techlap.service.impl;

import com.example.techlap.domain.User;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.UserRepository;
import com.example.techlap.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found";

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        // Đoạn code xử lí việc thêm role nhưng hiện giờ thì set = null trước
        user.setRole(null);
        // Role role =
        // this.roleRepository.findById(user.getRole().getId()).orElse(null);
        // if (role != null) {
        // user.setRole(role);
        // }else {
        // user.setRole(null);
        // }

        return userRepository.save(user);
    }

    @Override
    public User update(User user) throws Exception {
        User userInDB = this.findUserByIdOrThrow(user.getId());

        userInDB.setFullName(user.getFullName());
        userInDB.setPhone(user.getPhone());

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

        return res;
    }
}
