package com.example.techlap.controller;

import com.example.techlap.domain.User;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.criteria.CriteriaFilterUser;
import com.example.techlap.domain.request.ReqAdminChangePasswordDTO;
import com.example.techlap.domain.request.ReqChangePasswordDTO;
import com.example.techlap.domain.request.ReqPasswordTokenDTO;
import com.example.techlap.domain.request.ReqUpdateUserDTO;
import com.example.techlap.domain.respond.GenericResponse;
import com.example.techlap.domain.respond.DTO.ResCreateUserDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResUpdateUserDTO;
import com.example.techlap.domain.respond.DTO.ResUserDTO;
import com.example.techlap.service.EmailService;
import com.example.techlap.service.UserService;
import com.example.techlap.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final MessageSource messages;
    private final SecurityUtil securityUtil;

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws Exception {
        User newUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody ReqUpdateUserDTO reqUser) throws Exception {
        User currentUser = userService.update(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResUpdateUserDTO(currentUser));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable("id") long id) throws Exception {
        User user = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws Exception {
        this.userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResPaginationDTO> fetchAllUsers(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.userService.fetchAllUsersWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/users/filter")
    @ApiMessage("Filter users")
    public ResponseEntity<ResPaginationDTO> filterUsers(
            Pageable pageable,
            @RequestBody CriteriaFilterUser criteriaUser) throws Exception {
        ResPaginationDTO res = this.userService.filterUsers(pageable, criteriaUser);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/users/change-password/{id}")
    @ApiMessage("Change password")
    public ResponseEntity<Void> changePassword(@PathVariable("id") Long id,
            @RequestBody @Valid ReqAdminChangePasswordDTO changePasswordDTO) throws Exception {
        this.userService.adminChangePassword(id, changePasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/user/reset-password")
    @ApiMessage("Reset password")
    public ResponseEntity<GenericResponse> resetPassword(HttpServletRequest request,
            @RequestParam("email") String email) throws Exception {
        GenericResponse response = emailService.resetUserPassword(request, email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/user/change-password")
    @ApiMessage("Change password")
    public ResponseEntity<GenericResponse> changePasswordToken(Locale locale,
            @Valid @RequestBody ReqPasswordTokenDTO reqPasswordDTO)
            throws Exception {

        GenericResponse response = emailService.saveUserPassword(locale, reqPasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/users/me/change-password")
    @ApiMessage("Self change password")
    public ResponseEntity<Void> changeMyPassword(@RequestBody @Valid ReqChangePasswordDTO dto) throws Exception {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new UsernameNotFoundException("No authenticated user"));
        userService.changePasswordByEmail(email, dto);
        return ResponseEntity.ok().build();
    }

}
