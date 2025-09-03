package com.example.techlap.config;

import java.util.Collections;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.fetchUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/Password not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
