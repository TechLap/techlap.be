package com.example.techlap.domain.respond.DTO;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private List<ResProductDTO> products;

    @Getter
    @Setter
    public static class ProductDTO {
        private Long id;
        private String name;
    }
}
