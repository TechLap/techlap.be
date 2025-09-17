package com.example.techlap.domain.respond.DTO;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResBrandDTO {
    private long id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonIgnore
    private List<ProductDTO> products;

    @Getter
    @Setter
    public static class ProductDTO {
        private long id;
        private String name;
    }

}