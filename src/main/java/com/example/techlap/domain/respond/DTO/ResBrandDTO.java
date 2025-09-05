package com.example.techlap.domain.respond.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResBrandDTO {
    private long id;
    private String name;
    private List<ProductDTO> products;

    @Getter
    @Setter
    public static class ProductDTO {
        private long id;
        private String name;
    }

}
