package com.example.techlap.service.impl;

import com.example.techlap.domain.respond.DTO.ResCustomerLoginDTO;
import com.example.techlap.exception.ResourceNotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.User;
import com.example.techlap.domain.request.ReqLoginDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO.UserLogin;
import com.example.techlap.exception.IdInvalidException;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.repository.CustomerRepository;
import com.example.techlap.service.AuthService;
import com.example.techlap.service.CustomerService;
import com.example.techlap.service.UserService;
import com.example.techlap.util.SecurityUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final CustomerService customerService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String RESOURCE_NOT_FOUND_EXCEPTION_MESSAGE = "Resource not found";

    // Internal Login
    @Override
    public ResLoginDTO internalLogin(ReqLoginDTO loginDTO) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        final Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();

        User inDBUser = this.userService.fetchUserByEmail(loginDTO.getUsername());
        if (inDBUser != null) {
            UserLogin.RoleDTO roleDTO = new UserLogin.RoleDTO(inDBUser.getRole().getId(), inDBUser.getRole().getName());
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    inDBUser.getId(),
                    inDBUser.getEmail(),
                    inDBUser.getFullName(),
                    roleDTO);
            res.setUser(userLogin);
        }

        // tokens
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        res.setRefreshToken(refresh_token);

        // update refresh token once
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        return res;
    }

    // External Login
    @Override
    public ResCustomerLoginDTO externalLogin(ReqLoginDTO loginDTO) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        final Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResCustomerLoginDTO res = new ResCustomerLoginDTO();

        Customer inDBCustomer = this.customerService.fetchCustomerByEmail(loginDTO.getUsername());
        long totalCart = (inDBCustomer != null && inDBCustomer.getCart() != null) ? inDBCustomer.getCart().getSum() : 0;
        if (inDBCustomer != null) {
            ResCustomerLoginDTO.CustomerLogin customerLogin = new ResCustomerLoginDTO.CustomerLogin(
                    inDBCustomer.getId(),
                    inDBCustomer.getEmail(),
                    inDBCustomer.getFullName(),
                    totalCart,
                    inDBCustomer.getRole());
            res.setCustomer(customerLogin);
        }

        // tokens
        String access_token = this.securityUtil.createAccessTokenForCustomer(authentication.getName(), res);
        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshTokenForCustomer(loginDTO.getUsername(), res);
        res.setRefreshToken(refresh_token);

        // update refresh token once
        this.customerService.updateCustomerToken(refresh_token, loginDTO.getUsername());

        return res;
    }

    @Override
    public Customer register(Customer customer) throws Exception {

        // Check Username
        if (this.customerRepository.existsByEmail(customer.getEmail()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);

        // Save hashPassword
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        return this.customerRepository.save(customer);

    }

    @Override
    public ResLoginDTO getRefreshToken(String refresh_token) throws Exception {
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        // issue new token/set refresh token as cookies
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.fetchUserByEmail(email);
        if (currentUserDB != null) {
            UserLogin.RoleDTO roleDTO = new UserLogin.RoleDTO(currentUserDB.getRole().getId(),
                    currentUserDB.getRole().getName());
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getFullName(),
                    roleDTO);
            res.setUser(userLogin);
        }

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, res);

        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
        res.setRefreshToken(new_refresh_token);

        // update user
        this.userService.updateUserToken(new_refresh_token, email);

        return res;

    }

    @Override
    public ResCustomerLoginDTO getCustomerRefreshToken(String refresh_token) throws Exception {
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        Customer currentCustomer = this.customerService.getCustomerByRefreshTokenAndEmail(refresh_token, email);
        if (currentCustomer == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        // issue new token/set refresh token as cookies
        ResCustomerLoginDTO res = new ResCustomerLoginDTO();
        Customer currentCustomerDB = this.customerService.fetchCustomerByEmail(email);
        long totalCart = currentCustomerDB.getCart() != null ? currentCustomerDB.getCart().getSum() : 0;
        if (currentCustomerDB != null) {
            ResCustomerLoginDTO.CustomerLogin customerLogin = new ResCustomerLoginDTO.CustomerLogin(
                    currentCustomerDB.getId(),
                    currentCustomerDB.getEmail(),
                    currentCustomerDB.getFullName(),
                    totalCart,
                    currentCustomerDB.getRole());
            res.setCustomer(customerLogin);
        }

        // create access token
        String access_token = this.securityUtil.createAccessTokenForCustomer(email, res);

        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshTokenForCustomer(email, res);
        res.setRefreshToken(new_refresh_token);

        // update user
        this.customerService.updateCustomerToken(new_refresh_token, email);

        return res;

    }

    @Override
    public ResLoginDTO.UserGetAccount getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";

        User currentUserDB = this.userService.fetchUserByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            UserLogin.RoleDTO roleDTO = new UserLogin.RoleDTO(currentUserDB.getRole().getId(),
                    currentUserDB.getRole().getName());
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setFullName(currentUserDB.getFullName());
            userLogin.setRole(roleDTO);

            userGetAccount.setUser(userLogin);
            return userGetAccount;
        }else {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public ResCustomerLoginDTO.CustomerGetAccount getCustomer() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";

        Customer currentCustomerDB = this.customerService.fetchCustomerByEmail(email);
        ResCustomerLoginDTO.CustomerLogin customerLogin = new ResCustomerLoginDTO.CustomerLogin();
        ResCustomerLoginDTO.CustomerGetAccount customerGetAccount = new ResCustomerLoginDTO.CustomerGetAccount();

        if (currentCustomerDB != null) {
            customerLogin.setId(currentCustomerDB.getId());
            customerLogin.setEmail(currentCustomerDB.getEmail());
            customerLogin.setFullName(currentCustomerDB.getFullName());
            customerLogin.setTotalCart(currentCustomerDB.getCart().getSum());
            customerLogin.setRole(currentCustomerDB.getRole());

            customerGetAccount.setCustomer(customerLogin);
            return customerGetAccount;
        }else {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public Void logout() throws Exception {
        String email = this.securityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // Update refresh token = null
        this.userService.updateUserToken(null, email);

        return null;
    }

    @Override
    public Void logoutForCustomer() throws Exception {
        String email = this.securityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // Update refresh token = null
        this.customerService.updateCustomerToken(email, email);

        return null;
    }

}
