package com.example.techlap.service.impl;

import com.example.techlap.config.payment.VNPayConfig;
import com.example.techlap.domain.Order;
import com.example.techlap.domain.request.payment.VNPayRequest;
import com.example.techlap.service.VNPayService;
import com.example.techlap.util.payment.VNPayUtil;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnpConfig;

    @Override
    public String createPaymentUrl(Order order, String ipAddr) {
        Map<String, String> vnpParams = vnpConfig.getVNPayConfig();

        BigDecimal amountInVND = order.getTotalPrice(); // VD: 150000.50
        long vnpAmount = amountInVND.multiply(BigDecimal.valueOf(100)).longValue(); // 15000050

        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        String txnRef = order.getOrderCode() != null ? order.getOrderCode().replaceAll("\\D", "") : VNPayUtil.getRandomNumber(8);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + order.getOrderCode());
        vnpParams.put("vnp_IpAddr", ipAddr);

        String queryToSign = VNPayUtil.getPaymentURL(vnpParams, false);
        String secureHash = VNPayUtil.hmacSHA512(vnpConfig.getSecretKey(), queryToSign);

        String paymentUrl = vnpConfig.getVnpPayUrl()
        + "?" + queryToSign
        + "&vnp_SecureHash=" + secureHash;
        return paymentUrl;
    }

    @Override
    public boolean handlePaymentCallback(VNPayRequest vNPayRequest) {
        if (!validateCallback(vNPayRequest)) {
            return false;
        }

        String responseCode = vNPayRequest.getVnp_ResponseCode();
        if ("00".equals(responseCode)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean validateCallback(VNPayRequest vnPayRequest) {
        String vnpSecureHash = vnPayRequest.getVnp_SecureHash();

        // Tạo lại query string (bỏ vnp_SecureHash)
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Amount", vnPayRequest.getVnp_Amount());
        vnp_Params.put("vnp_BankCode", vnPayRequest.getVnp_BankCode());
        vnp_Params.put("vnp_BankTranNo", vnPayRequest.getVnp_BankTranNo());
        vnp_Params.put("vnp_CardType", vnPayRequest.getVnp_CardType());
        vnp_Params.put("vnp_OrderInfo", vnPayRequest.getVnp_OrderInfo());
        vnp_Params.put("vnp_PayDate", vnPayRequest.getVnp_PayDate());
        vnp_Params.put("vnp_ResponseCode", vnPayRequest.getVnp_ResponseCode());
        vnp_Params.put("vnp_TmnCode", vnPayRequest.getVnp_TmnCode());
        vnp_Params.put("vnp_TransactionNo", vnPayRequest.getVnp_TransactionNo());
        vnp_Params.put("vnp_TransactionStatus", vnPayRequest.getVnp_TransactionStatus());
        vnp_Params.put("vnp_TxnRef", vnPayRequest.getVnp_TxnRef());

        String queryString = VNPayUtil.getPaymentURL(vnp_Params, false);
        String secureHash = VNPayUtil.hmacSHA512(vnpConfig.getSecretKey(), queryString);

        return vnpSecureHash.equals(secureHash);
    }

}