package com.example.techlap.service.impl;


import org.springframework.stereotype.Service;

import com.example.techlap.domain.PaymentTransaction;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.PaymentTransactionRepository;
import com.example.techlap.service.PaymentTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentTransactionServiceImpl implements PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PaymentTransaction getPaymentTransactionByOrderCode(String orderCode) {
        return this.paymentTransactionRepository.findByOrderCode(orderCode).orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found"));
    }

    @Override
    public PaymentTransaction update(PaymentTransaction paymentTransaction) {
        return this.paymentTransactionRepository.save(paymentTransaction);
    }
}
