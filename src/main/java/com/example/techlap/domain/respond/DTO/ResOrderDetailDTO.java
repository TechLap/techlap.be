package com.example.techlap.domain.respond.DTO;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderDetailDTO {
    private Long id;
    private BigDecimal price;
    private Long quantity;
    private Instant createdAt;
    private Instant updatedAt;
    private ResOrderDetailDTO.ProductDTO product;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDTO {
        private Long id;
        private String name;
        private BigDecimal price;
        private double discount;
        private ResOrderDetailDTO.CategoryDTO category;
        private ResOrderDetailDTO.BrandDTO brand;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandDTO {
        private Long id;
        private String name;
    }
}
