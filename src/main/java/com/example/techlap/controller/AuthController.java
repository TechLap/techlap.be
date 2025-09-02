package com.example.techlap.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.techlap.domain.User;
import com.example.techlap.domain.request.ReqLoginDTO;
import com.example.techlap.domain.respond.DTO.ResCreateUserDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;
import com.example.techlap.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final JwtEncoder jwtEncoder;

    private final AuthService authService;

    public AuthController(AuthService authService, JwtEncoder jwtEncoder) {
        this.authService = authService;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) throws Exception {

        User newUser = this.authService.register(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO body) throws Exception {
        ResLoginDTO res = this.authService.login(body);
        return ResponseEntity.ok(res);
    }

}
