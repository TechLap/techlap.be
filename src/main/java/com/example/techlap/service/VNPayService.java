package com.example.techlap.service;

import com.example.techlap.domain.Order;
import com.example.techlap.domain.request.payment.VNPayRequest;

public interface VNPayService {
    String createPaymentUrl(Order order, String ipAddr);

    boolean handlePaymentCallback(VNPayRequest vNPayRequest);

    boolean validateCallback(VNPayRequest vNPayRequest);
}
