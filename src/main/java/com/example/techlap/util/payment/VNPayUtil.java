package com.example.techlap.util.payment;

import java.util.Random;

import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.example.techlap.domain.request.payment.VNPayRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.net.URLEncoder;
import jakarta.servlet.http.HttpServletRequest;

public class VNPayUtil {
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        try {
            String ip = request.getHeader("X-FORWARDED-FOR");
            if (ip != null && !ip.isBlank()) {
                // Nếu có nhiều IP, lấy IP đầu tiên
                ip = ip.split(",")[0].trim();
            } else {
                ip = request.getRemoteAddr();
            }

            // Chuẩn hóa IPv6 localhost về IPv4
            if ("::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                return "127.0.0.1";
            }

            // Nếu là địa chỉ IPv6 khác, có thể ép về 127.0.0.1 khi sandbox kén IP
            if (ip.contains(":")) {
                return "127.0.0.1";
            }

            return ip;
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getPaymentURL(Map<String, String> paramsMap, boolean encodeKey) {
        return paramsMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> (encodeKey ? URLEncoder.encode(entry.getKey(),
                        StandardCharsets.US_ASCII)
                        : entry.getKey()) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }

    public static VNPayRequest convertToVNPayRequest(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        VNPayRequest vNPayRequest = new VNPayRequest();
        vNPayRequest.setVnp_Amount(params.get("vnp_Amount"));
        vNPayRequest.setVnp_BankCode(params.get("vnp_BankCode"));
        vNPayRequest.setVnp_BankTranNo(params.get("vnp_BankTranNo"));
        vNPayRequest.setVnp_CardType(params.get("vnp_CardType"));
        vNPayRequest.setVnp_PayDate(params.get("vnp_PayDate"));
        vNPayRequest.setVnp_ResponseCode(params.get("vnp_ResponseCode"));
        vNPayRequest.setVnp_SecureHash(params.get("vnp_SecureHash"));
        vNPayRequest.setVnp_TmnCode(params.get("vnp_TmnCode"));
        vNPayRequest.setVnp_TransactionNo(params.get("vnp_TransactionNo"));
        vNPayRequest.setVnp_TransactionStatus(params.get("vnp_TransactionStatus"));
        vNPayRequest.setVnp_TxnRef(params.get("vnp_TxnRef"));
        vNPayRequest.setVnp_OrderInfo(params.get("vnp_OrderInfo"));
        return vNPayRequest;
    }
}