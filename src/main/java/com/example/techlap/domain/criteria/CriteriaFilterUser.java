package com.example.techlap.domain.criteria;

import com.example.techlap.domain.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaFilterUser {
    private String email;
    private String fullName;
    private String createdAt;
    private String createdBy;
    private String phone;
    private String address;
    private Role role;

}
