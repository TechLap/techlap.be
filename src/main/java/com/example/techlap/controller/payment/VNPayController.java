package com.example.techlap.controller.payment;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.techlap.domain.PaymentTransaction;
import com.example.techlap.domain.enums.OrderStatus;
import com.example.techlap.domain.enums.PaymentStatus;
import com.example.techlap.domain.request.payment.VNPayRequest;
import com.example.techlap.service.OrderService;
import com.example.techlap.service.PaymentTransactionService;
import com.example.techlap.service.VNPayService;
import com.example.techlap.util.payment.VNPayUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;
    private final PaymentTransactionService paymentTransactionService;
    private final OrderService orderService;

    @GetMapping("/payment/vnpay-callback")
    public ResponseEntity<String> callback(@RequestParam Map<String, String> params) throws Exception {
        VNPayRequest vnPayRequest = VNPayUtil.convertToVNPayRequest(params);
        String txnRef = params.get("vnp_TxnRef");
        String orderCode = txnRef != null ? "TLS-" + txnRef : null;
        PaymentTransaction paymentTransaction = this.paymentTransactionService
                .getPaymentTransactionByOrderCode(orderCode);
        if (this.vnPayService.handlePaymentCallback(vnPayRequest)) {
            paymentTransaction.setStatus(PaymentStatus.SUCCESS);
            paymentTransaction.setResponseCode(vnPayRequest.getVnp_ResponseCode());
            this.paymentTransactionService.update(paymentTransaction);
            this.orderService.updateOrderStatusByOrderCode(orderCode, OrderStatus.PAID);
            return ResponseEntity.ok("Payment successful");
        } else {
            paymentTransaction.setStatus(PaymentStatus.FAILED);
            paymentTransaction.setResponseCode(vnPayRequest.getVnp_ResponseCode());
            this.paymentTransactionService.update(paymentTransaction);
            this.orderService.updateOrderStatusByOrderCode(orderCode, OrderStatus.CANCELLED);
            return ResponseEntity.badRequest().body("Payment failed");
        }
    }

    @PostMapping("/payment/vnpay-verify")
    public ResponseEntity<String> verifyReturn(@RequestBody Map<String, String> params) throws Exception {
        System.out.println("=== VNPAY VERIFY (RETURN) DEBUG ===");
        System.out.println("Received params: " + params);

        VNPayRequest vnPayRequest = VNPayUtil.convertToVNPayRequest(params);
        if (vnPayRequest == null) {
            return ResponseEntity.badRequest().body("Invalid params");
        }

        boolean valid = this.vnPayService.validateCallback(vnPayRequest);
        String txnRef = params.get("vnp_TxnRef");
        String orderCode = txnRef != null ? "TLS-" + txnRef : null;

        PaymentTransaction paymentTransaction = this.paymentTransactionService
                .getPaymentTransactionByOrderCode(orderCode);

        if (!valid) {
            paymentTransaction.setStatus(PaymentStatus.FAILED);
            paymentTransaction.setResponseCode(vnPayRequest.getVnp_ResponseCode());
            this.paymentTransactionService.update(paymentTransaction);
            this.orderService.updateOrderStatusByOrderCode(orderCode, OrderStatus.CANCELLED);
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("00".equals(vnPayRequest.getVnp_ResponseCode())) {
            paymentTransaction.setStatus(PaymentStatus.SUCCESS);
            paymentTransaction.setResponseCode(vnPayRequest.getVnp_ResponseCode());
            this.paymentTransactionService.update(paymentTransaction);
            this.orderService.updateStockAfterPayment(orderCode);
            return ResponseEntity.ok("Payment verified and updated");
        } else {
            paymentTransaction.setStatus(PaymentStatus.FAILED);
            paymentTransaction.setResponseCode(vnPayRequest.getVnp_ResponseCode());
            this.paymentTransactionService.update(paymentTransaction);
            this.orderService.updateOrderStatusByOrderCode(orderCode, OrderStatus.CANCELLED);
            return ResponseEntity.ok("Payment failed");
        }
    }
}
