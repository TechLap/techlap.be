package com.example.techlap.domain.respond.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.example.techlap.domain.enums.OrderStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderDTO {
    private Long id;
    private String orderCode;
    private BigDecimal totalPrice;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String note;
    private OrderStatus status;
    private String paymentMethod; // ✅ Thêm phương thức thanh toán
    private String paymentStatus; // ✅ Thêm trạng thái thanh toán
    private String paymentUrl;
    private Instant createdAt;
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


   
