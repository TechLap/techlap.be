package com.example.techlap.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaFilterOrder {
    private String orderCode;
    private String status;
    private String createdAt;
    private CustomerOrderDTO customer;

    @Getter
    @Setter
    public static class CustomerOrderDTO {
        private long id;
        private String fullName;
    }
}
