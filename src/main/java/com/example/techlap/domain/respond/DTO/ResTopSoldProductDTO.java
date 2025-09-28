package com.example.techlap.domain.respond.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResTopSoldProductDTO {
    private long id;
    private String name;
    private long sold;
    private BigDecimal price;

}
