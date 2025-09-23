package com.example.techlap.service;

import com.example.techlap.domain.PaymentTransaction;

public interface PaymentTransactionService {
    PaymentTransaction getPaymentTransactionByOrderCode(String orderCode);
    PaymentTransaction update(PaymentTransaction paymentTransaction);
}
