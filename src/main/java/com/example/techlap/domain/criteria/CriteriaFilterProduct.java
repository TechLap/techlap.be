package com.example.techlap.domain.criteria;


import java.math.BigDecimal;

import com.example.techlap.domain.Brand;
import com.example.techlap.domain.Category;
import com.example.techlap.domain.enums.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaFilterProduct {

    private String name;
    private String createdAt;
    private Enum<ProductStatus> status;
    private Brand brand;
    private Category category;
    private PriceRange priceRange;

    @Getter
    @Setter
    public static class PriceRange {
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
    }
}
