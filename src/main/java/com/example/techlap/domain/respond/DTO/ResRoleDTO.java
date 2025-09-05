package com.example.techlap.domain.respond.DTO;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResRoleDTO {
    private long id;
    private String name;
    private String description;
    private List<PermissionDTO> permissions;
    private List<UserDTO> users;

    @Getter
    @Setter
    public static class PermissionDTO {
        private long id;
        private String name;
    }

    @Data
    public static class UserDTO {
        private long id;
        private String fullName;
    }
}