package com.example.techlap.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.techlap.constant.JwtConstants;
import com.example.techlap.domain.User;
import com.example.techlap.domain.request.ReqLoginDTO;
import com.example.techlap.domain.respond.DTO.ResCreateUserDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.repository.UserRepository;
import com.example.techlap.service.AuthService;
import com.example.techlap.service.UserService;
import com.example.techlap.util.SecurityUtil;

@Service
public class AuthServceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final JwtConstants jwtConstants;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found";

    public AuthServceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityUtil securityUtil,
            AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService,
            JwtConstants jwtConstants) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.jwtConstants = jwtConstants;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Override
    public ResLoginDTO login(ReqLoginDTO loginDTO) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();

        User inDBUser = this.userService.fetchUserByEmail(loginDTO.getUsername());
        if (inDBUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    inDBUser.getId(),
                    inDBUser.getEmail(),
                    inDBUser.getFullName(),
                    inDBUser.getRole());
            res.setUser(userLogin);
        }
        // create accessToken
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

        // create refreshToken
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        // update user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        // set Cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(this.jwtConstants.refreshTokenExpiration)
                .build();

        return res;
    }

    @Override
    public User register(User user) throws Exception {

        // Check Username
        if (this.userRepository.existsByEmail(user.getEmail()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);

        // Save hashPassword
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return this.userRepository.save(user);

    }
}
