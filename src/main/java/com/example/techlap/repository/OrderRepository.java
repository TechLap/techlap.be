package com.example.techlap.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT coalesce(sum(o.totalPrice), 0) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') as month, " +
       "SUM(o.totalPrice) as revenue " +
       "FROM Order o " +
       "WHERE o.status = 'PAID' " +
       "AND YEAR(o.createdAt) = :year " +
       "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') " +
       "ORDER BY month")
    List<Object[]> findMonthlyRevenue(@Param("year") int year);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrderByStatus();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PAID'")
    long countOrderPaid();
}
