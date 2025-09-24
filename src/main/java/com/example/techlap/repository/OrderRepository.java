package com.example.techlap.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.example.techlap.domain.Order;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> { 

    @Query("SELECT coalesce(sum(od.price * od.quantity), 0) FROM OrderDetail od WHERE od.order.id = :orderId")
    BigDecimal calculateTotalPrice(Long orderId);

    Optional<Order> findByOrderCode(String orderCode);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
  
    Page<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
}
