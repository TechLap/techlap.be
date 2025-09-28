package com.example.techlap.service;


import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Order;
import com.example.techlap.domain.criteria.CriteriaFilterOrder;
import com.example.techlap.domain.enums.OrderStatus;
import com.example.techlap.domain.request.ReqCreateOrder;
import com.example.techlap.domain.respond.DTO.ResMonthlyRevenueDTO;
import com.example.techlap.domain.respond.DTO.ResOrderDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResStatusOrderAnalyticsDTO;

import jakarta.servlet.http.HttpServletRequest;


public interface OrderService {
    ResOrderDTO create(ReqCreateOrder order, HttpServletRequest request) throws Exception;

    void updateOrderStatus(Long id, OrderStatus status) throws Exception;

    void updateOrderStatusByOrderCode(String orderCode, OrderStatus status) throws Exception;

    void updateStockAfterPayment(String orderCode) throws Exception;

    ResOrderDTO getByOrderCode(String orderCode) throws Exception;

    ResPaginationDTO getOrdersWithPagination(Pageable pageable) throws Exception;

    ResOrderDTO updateOrderInfo(Order order) throws Exception;

    ResPaginationDTO filterOrders(Pageable pageable, CriteriaFilterOrder criteriaFilterOrder) throws Exception;

    List<ResMonthlyRevenueDTO> getMonthlyRevenue(Integer year) throws Exception;

    ResStatusOrderAnalyticsDTO getStatusOrderAnalytics() throws Exception;
}