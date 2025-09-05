package com.example.techlap.domain.respond.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateUserDTO {

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
