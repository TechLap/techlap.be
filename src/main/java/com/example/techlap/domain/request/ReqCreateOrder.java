package com.example.techlap.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReqCreateOrder {
    @NotBlank(message = "Receiver name is required")
    private String receiverName;

    @NotBlank(message = "Receiver address is required")
    private String receiverAddress;

    @NotBlank(message = "Receiver phone is required")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Invalid phone number")
    private String receiverPhone;

    private String note; // Optional - ghi chú cho đơn hàng

    @NotNull(message = "Payment method is required")
    private String paymentMethod; // VNPAY, COD, etc.
}
