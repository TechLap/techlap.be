package com.example.techlap.domain.respond.DTO;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResRegisterCustomerDTO {

    private long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Instant createdAt;
    private RoleUser role;

    @Setter
    @Getter
    public static class RoleUser {
        private long id;
        private String name;
    }
}
