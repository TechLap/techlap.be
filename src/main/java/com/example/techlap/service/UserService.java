package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.User;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;

public interface UserService {
    // Create a user
    User create(User user) throws Exception;

    // Update a user
    User update(User user) throws Exception;

    // Find a user by id
    User fetchUserById(long id) throws Exception;

    // Find a user by username
    User fetchUserByEmail(String email);

    // Find all user with pagination
    ResPaginationDTO fetchAllUsersWithPagination(Pageable pageable) throws Exception;

    // Delete a user by id
    void delete(long id) throws Exception;

    void updateUserToken(String token, String email) throws Exception;

    User getUserByRefreshTokenAndEmail(String token, String email) throws Exception;
}
