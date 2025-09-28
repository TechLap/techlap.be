package com.example.techlap.domain.respond.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCartDTO {
    private long id;
    private int sum;
    private ResCustomerDTO customer;
    private List<CartDetailDTO> cartDetails;

    @Getter
    @Setter
    public static class CartDetailDTO {
        private long id;
        private long quantity;
        private BigDecimal price;
        private ProductDTO product;
    }

    @Getter
    @Setter
    public static class ProductDTO {
        private long id;
        private String name;
        private BigDecimal price;
        private double discount;
        private long stock;
        private String description;
        private String image;
        private String status;
        private ResProductDTO.CategoryDTO category;
    }

}