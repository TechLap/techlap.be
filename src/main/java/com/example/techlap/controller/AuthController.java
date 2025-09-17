package com.example.techlap.controller;

import com.example.techlap.domain.respond.DTO.ResCustomerLoginDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.techlap.constant.JwtConstants;
import com.example.techlap.domain.Customer;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.request.ReqLoginDTO;
import com.example.techlap.domain.respond.DTO.ResLoginDTO;
import com.example.techlap.service.AuthService;
import com.example.techlap.util.SecurityUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

        private final AuthService authService;
        private final JwtConstants jwtConstants;
        private final SecurityUtil securityUtil;

        @PostMapping("/register")
        public ResponseEntity<Customer> register(@Valid @RequestBody Customer customer) throws Exception {

                Customer newCustomer = this.authService.register(customer);

                return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
        }

        // External Login
        @PostMapping("/login")
        public ResponseEntity<ResCustomerLoginDTO> login(@Valid @RequestBody ReqLoginDTO body) throws Exception {
                ResCustomerLoginDTO res = this.authService.externalLogin(body);
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", res.getRefreshToken())
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(jwtConstants.refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        // Interal Login
        @PostMapping("/admin/login")
        public ResponseEntity<ResLoginDTO> internalLogin(@Valid @RequestBody ReqLoginDTO body) throws Exception {
                ResLoginDTO res = this.authService.internalLogin(body);
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", res.getRefreshToken())
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(jwtConstants.refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/auth/account")
        @ApiMessage("Fetch account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() throws Exception {
                ResLoginDTO.UserGetAccount userAccount = this.authService.getAccount();
                return ResponseEntity.ok().body(userAccount);
        }

        @GetMapping("/auth/customers/account")
        @ApiMessage("Fetch account")
        public ResponseEntity<ResCustomerLoginDTO.CustomerGetAccount> getCustomer() throws Exception {
                ResCustomerLoginDTO.CustomerGetAccount customerAccount = this.authService.getCustomer();
                return ResponseEntity.ok().body(customerAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get User by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token)
                        throws Exception {
                ResLoginDTO res = this.authService.getRefreshToken(refresh_token);
                // set cookie
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", res.getRefreshToken())
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(jwtConstants.refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/auth/customers/refresh")
        @ApiMessage("Get Customer by refresh token")
        public ResponseEntity<ResCustomerLoginDTO> getCustomerRefreshToken(
                        @CookieValue(name = "refresh_token") String refresh_token)
                        throws Exception {
                ResCustomerLoginDTO res = this.authService.getCustomerRefreshToken(refresh_token);
                // set cookie
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", res.getRefreshToken())
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(jwtConstants.refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout User")
        public ResponseEntity<Void> logout() throws Exception {
                this.authService.logout();
                // remove refresh token
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }
}
