package com.example.techlap.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.techlap.domain.Role;
import com.example.techlap.domain.User;
import com.example.techlap.domain.PasswordResetToken;
import com.example.techlap.domain.QUser;
import com.example.techlap.domain.criteria.CriteriaFilterUser;
import com.example.techlap.domain.request.ReqChangePasswordDTO;
import com.example.techlap.domain.request.ReqUpdateUserDTO;
import com.example.techlap.domain.respond.DTO.ResCreateUserDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResUpdateUserDTO;
import com.example.techlap.domain.respond.DTO.ResUserDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.PasswordResetTokenRepository;
import com.example.techlap.repository.UserRepository;
import com.example.techlap.service.RoleService;
import com.example.techlap.service.UserService;
import com.querydsl.core.BooleanBuilder;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
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

    @Override
    public ResPaginationDTO filterUsers(Pageable pageable, CriteriaFilterUser criteriaUser) throws Exception {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteriaUser.getFullName() != null && !criteriaUser.getFullName().isEmpty()) {
            builder.and(qUser.fullName.containsIgnoreCase(criteriaUser.getFullName()));
        }
        if (criteriaUser.getEmail() != null && !criteriaUser.getEmail().isEmpty()) {
            builder.and(qUser.email.containsIgnoreCase(criteriaUser.getEmail()));
        }
        if (criteriaUser.getPhone() != null && !criteriaUser.getPhone().isEmpty()) {
            builder.and(qUser.phone.containsIgnoreCase(criteriaUser.getPhone()));
        }
        if (criteriaUser.getAddress() != null && !criteriaUser.getAddress().isEmpty()) {
            builder.and(qUser.address.containsIgnoreCase(criteriaUser.getAddress()));
        }
        if (criteriaUser.getRole() != null) {
            builder.and(qUser.role.eq(criteriaUser.getRole()));
        }
        if (criteriaUser.getCreatedAt() != null && !criteriaUser.getCreatedAt().isEmpty()) {
            LocalDate localDate = LocalDate.parse(criteriaUser.getCreatedAt());
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Instant from = localDate.atStartOfDay(defaultZoneId).toInstant();
            Instant to = localDate.plusDays(1).atStartOfDay(defaultZoneId).minusNanos(1).toInstant();
            builder.and(qUser.createdAt.between(from, to));
        }

        Page<User> userPage = userRepository.findAll(builder, pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        meta.setPage(userPage.getNumber() + 1);
        meta.setPageSize(userPage.getSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());
        res.setMeta(meta);
        List<ResUserDTO> listUser = userPage.getContent()
                .stream().map(this::convertToResUserDTO)
                .collect(Collectors.toList());
        res.setResult(listUser);
        return res;
    }

    @Override
    public void changePassword(Long id, ReqChangePasswordDTO password) throws Exception {
        User userInDB = this.findUserByIdOrThrow(id);

        if (checkIfValidOldPassword(userInDB, password.getOldPassword())) {
            if (password.getNewPassword().equals(password.getReNewPassword())) {
                userInDB.setPassword(passwordEncoder.encode(password.getNewPassword()));
                userRepository.save(userInDB);
            } else {
                throw new IllegalArgumentException("New password and re-new password do not match");
            }
        } else {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();
    }

    @Override
    public User getUserByPasswordResetToken(String token) throws Exception {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        User user = passwordResetToken.getUser();
        return user;
    }

}
