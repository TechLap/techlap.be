package com.example.techlap.domain.respond.DTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResOrderDTO {
    private long id;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String note;
    private String status;
    private String paymentMethod;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
    private String totalPrice;
    private ResCustomerDTO customer;
    private List<ResOrderDetailDTO> orderDetails;

    @Getter
    @Setter
    public static class ResOrderDetailDTO {
        private long id;
        private long quantity;
        private BigDecimal price;
        private ResCartDTO.ProductDTO product;
    }
}
