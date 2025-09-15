package com.example.techlap.domain.respond.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCustomerDTO {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long totalSpending;
    private Long totalOrders;

    private RoleDTO roles;
    private List<OrderDTO> orders;
    private CartDTO cart;

    @Getter
    @Setter
    public static class OrderDTO {
        private long id;
        private String orderNumber;
    }

    @Getter
    @Setter
    public static class RoleDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class CartDTO {
        private long id;
    }
}
