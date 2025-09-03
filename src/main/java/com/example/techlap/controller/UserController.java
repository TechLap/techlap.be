package com.example.techlap.controller;

import com.example.techlap.domain.User;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.UserService;
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
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws Exception {
        User newUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws Exception {
        User currentUser = userService.update(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(currentUser);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<User> fetchUserById(@PathVariable("id") long id) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchUserById(id));
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

}
