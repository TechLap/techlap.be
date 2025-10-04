package com.example.techlap.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.techlap.domain.Order;
import com.example.techlap.domain.criteria.CriteriaFilterOrder;
import com.example.techlap.domain.request.ReqCreateOrder;
import com.example.techlap.domain.respond.DTO.ResMonthlyRevenueDTO;
import com.example.techlap.domain.respond.DTO.ResOrderDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResStatusOrderAnalyticsDTO;
import com.example.techlap.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<ResOrderDTO> createOrder(@RequestBody ReqCreateOrder order, HttpServletRequest request)
            throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.create(order, request));
    }

    @GetMapping("/orders/code/{orderCode}")
    public ResponseEntity<ResOrderDTO> getByCode(@PathVariable String orderCode) throws Exception {
        return ResponseEntity.ok(this.orderService.getByOrderCode(orderCode));
    }

    @GetMapping("/orders")
    public ResponseEntity<ResPaginationDTO> getOrdersWithPagination(Pageable pageable) throws Exception {
        return ResponseEntity.ok(this.orderService.getOrdersWithPagination(pageable));
    }

    @PutMapping("/orders")
    public ResponseEntity<ResOrderDTO> updateOrderInfo(@RequestBody Order order) throws Exception {
        return ResponseEntity.ok(this.orderService.updateOrderInfo(order));
    }

    @PostMapping("/orders/filter")
    public ResponseEntity<ResPaginationDTO> filterOrders(Pageable pageable,
            @RequestBody CriteriaFilterOrder criteriaFilterOrder) throws Exception {
        return ResponseEntity.ok(this.orderService.filterOrders(pageable, criteriaFilterOrder));
    }

    @GetMapping("/orders/monthly-revenue")
    public ResponseEntity<List<ResMonthlyRevenueDTO>> getMonthlyRevenue(
            @RequestParam(value = "year", required = false) Integer year)
            throws Exception {
        return ResponseEntity.ok(this.orderService.getMonthlyRevenue(year));
    }

    @GetMapping("/orders/status/count")
    public ResponseEntity<ResStatusOrderAnalyticsDTO> getOrderStatusCount() throws Exception {
        return ResponseEntity.ok(this.orderService.getStatusOrderAnalytics());
    }
}
