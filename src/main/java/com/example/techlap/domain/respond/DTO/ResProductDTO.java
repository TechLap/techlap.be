package com.example.techlap.domain.respond.DTO;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.techlap.domain.enums.ProductStatus;

import lombok.Data;

@Data
public class ResProductDTO {
    private long id;
    private String name;
    private BigDecimal price;
    private double discount;
    private long stock;
    private long sold;
    private String description;
    private String image;
    private ProductStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    // Nested DTOs for related entities
    private CategoryDTO category;
    private BrandDTO brand;

    @Data
    public static class CategoryDTO {
        private long id;
        private String name;
    }

    @Data
    public static class BrandDTO {
        private long id;
        private String name;
    }
}