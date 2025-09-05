package com.example.techlap.domain.respond.DTO;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class ResPermissionDTO {

    private long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private Instant updatedAt;
    private Instant createdAt;
    private String createdBy;
    private String updatedBy;

    private List<RoleDTO> roles;

    @Data
    public static class RoleDTO {
        private long id;
        private String name;
    }

}
