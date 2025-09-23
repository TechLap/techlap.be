package com.example.techlap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.techlap.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

}
