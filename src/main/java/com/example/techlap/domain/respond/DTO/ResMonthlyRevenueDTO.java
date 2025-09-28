package com.example.techlap.domain.respond.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResMonthlyRevenueDTO {
    private String month;
    private BigDecimal revenue;
}