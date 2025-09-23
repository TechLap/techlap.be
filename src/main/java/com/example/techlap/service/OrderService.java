package com.example.techlap.service;


import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.enums.OrderStatus;
import com.example.techlap.domain.request.ReqCreateOrder;
import com.example.techlap.domain.respond.DTO.ResOrderDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;

import jakarta.servlet.http.HttpServletRequest;


public interface OrderService {
    ResOrderDTO create(ReqCreateOrder order, HttpServletRequest request) throws Exception;

    void updateOrderStatus(Long id, OrderStatus status) throws Exception;

    void updateOrderStatusByOrderCode(String orderCode, OrderStatus status) throws Exception;

    ResOrderDTO getByOrderCode(String orderCode) throws Exception;

    ResPaginationDTO getOrdersWithPagination(Pageable pageable) throws Exception;
}
