package com.example.techlap.domain.respond.DTO;

import lombok.Data;
import java.time.Instant;

@Data
public class ResUpdateUserDTO {
    private Long id;
    private String fullName;
    private String address;
    private String phone;
    private Instant updatedAt;
    private RoleUser role;

    @Data
    public static class RoleUser {
        private Long id;
        private String name;
    }
}