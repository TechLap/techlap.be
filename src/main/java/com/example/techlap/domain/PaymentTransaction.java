package com.example.techlap.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.techlap.domain.enums.PaymentStatus;
import com.example.techlap.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_transactions")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "payment_method")
    private String paymentMethod; // VNPAY, MOMO, ZALOPAY, etc.

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency = "VND";

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, SUCCESS, FAILED, CANCELLED

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "response_message")
    private String responseMessage;

    // Generic fields - không phụ thuộc vào payment gateway cụ thể
    @Column(name = "external_transaction_id") // VNPay: vnp_TxnRef, Momo: orderId
    private String externalTransactionId;

    @Column(name = "external_response_data", columnDefinition = "TEXT") // Lưu toàn bộ response JSON
    private String externalResponseData;

    @Column(name = "payment_url", columnDefinition = "TEXT") // URL để redirect
    private String paymentUrl;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "description")
    private String description;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : " ";
    }
}