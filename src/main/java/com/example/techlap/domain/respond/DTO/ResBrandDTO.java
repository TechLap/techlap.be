package com.example.techlap.domain.respond.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResBrandDTO {
    private long id;
    private String name;

    @JsonIgnore
    private List<ProductDTO> products;

    @Getter
    @Setter
    public static class ProductDTO {
        private long id;
        private String name;
    }

}
