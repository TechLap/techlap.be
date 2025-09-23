package com.example.techlap.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.techlap.domain.request.ReqCreateOrder;
import com.example.techlap.domain.respond.DTO.ResOrderDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/orders")
    public ResponseEntity<ResOrderDTO> createOrder(@RequestBody ReqCreateOrder order, HttpServletRequest request) throws Exception {
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
}
