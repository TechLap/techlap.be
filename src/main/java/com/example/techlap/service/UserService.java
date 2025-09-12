package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.User;
import com.example.techlap.domain.criteria.CriteriaFilterUser;
import com.example.techlap.domain.request.ReqChangePasswordDTO;
import com.example.techlap.domain.request.ReqUpdateUserDTO;
import com.example.techlap.domain.respond.DTO.ResCreateUserDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResUpdateUserDTO;
import com.example.techlap.domain.respond.DTO.ResUserDTO;

public interface UserService {
    // Create a user
    User create(User user) throws Exception;

    // Update a user
    User update(ReqUpdateUserDTO reqUser) throws Exception;

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

    ResUserDTO convertToResUserDTO(User user) throws Exception;

    ResCreateUserDTO convertToResCreateUserDTO(User user) throws Exception;

    ResUpdateUserDTO convertToResUpdateUserDTO(User user) throws Exception;

    ResPaginationDTO filterUsers(Pageable pageable, CriteriaFilterUser criteriaUser) throws Exception;

    void changePassword(Long id, ReqChangePasswordDTO changePasswordDTO) throws Exception;

    boolean checkIfValidOldPassword(User user, String oldPassword);
    
    void changeUserPassword(User user, String newPassword);

    User getUserByPasswordResetToken(String token) throws Exception;
}
