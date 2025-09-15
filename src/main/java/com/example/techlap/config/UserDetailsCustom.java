package com.example.techlap.config;

import java.util.Collections;

import com.example.techlap.domain.Customer;
import com.example.techlap.service.CustomerService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.techlap.domain.User;
import com.example.techlap.service.UserService;

import lombok.AllArgsConstructor;

@Component("userDetailsService")
@AllArgsConstructor
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;
    private final CustomerService customerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check trong bảng User
        User user = userService.fetchUserByEmail(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        // Check trong bảng Customer
        Customer customer = customerService.fetchCustomerByEmail(username);
        if (customer != null) {
            return new org.springframework.security.core.userdetails.User(
                    customer.getEmail(),
                    customer.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        }

        throw new UsernameNotFoundException("Username/Password not found");
    }
}
