package com.example.techlap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.techlap.domain.Cart;
import com.example.techlap.domain.CartDetail;
import com.example.techlap.domain.Customer;
import com.example.techlap.domain.Order;
import com.example.techlap.domain.OrderDetail;
import com.example.techlap.domain.PaymentTransaction;
import com.example.techlap.domain.Product;
import com.example.techlap.domain.QOrder;
import com.example.techlap.domain.criteria.CriteriaFilterOrder;
import com.example.techlap.domain.enums.OrderStatus;
import com.example.techlap.domain.enums.PaymentStatus;
import com.example.techlap.domain.request.ReqCreateOrder;
import com.example.techlap.domain.respond.DTO.ResMonthlyRevenueDTO;
import com.example.techlap.domain.respond.DTO.ResOrderDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO.Meta;
import com.example.techlap.domain.respond.DTO.ResStatusOrderAnalyticsDTO;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.exception.StockNotEnoughException;
import com.example.techlap.repository.CartDetailRepository;
import com.example.techlap.repository.CartRepository;
import com.example.techlap.repository.CustomerRepository;
import com.example.techlap.repository.OrderRepository;
import com.example.techlap.repository.ProductRepository;
import com.example.techlap.service.OrderService;
import com.example.techlap.service.ProductService;
import com.example.techlap.service.EmailService;
import com.example.techlap.util.SecurityUtil;
import com.example.techlap.service.VNPayService;
import com.example.techlap.util.payment.VNPayUtil;
import com.querydsl.core.BooleanBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final VNPayService vnPayService;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final ProductService productService;

    private Order findOrderByOrderCodeOrThrow(String orderCode) {
        return this.orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private ResOrderDTO convertToResOrderDTO(Order order) {
        ResOrderDTO dto = this.modelMapper.map(order, ResOrderDTO.class);
        return dto;
    }

    private String generateOrderCode() {
        String random = VNPayUtil.getRandomNumber(8);
        return "TLS-" + random;
    }

    @Override
    public void updateOrderStatusByOrderCode(String orderCode, OrderStatus status) throws Exception {
        Order order = this.orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        this.orderRepository.save(order);
    }

    public ResOrderDTO getByOrderCode(String orderCode) throws Exception {
        Order order = this.orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return this.convertToResOrderDTO(order);
    }

    @Override
    @Transactional
    public ResOrderDTO create(ReqCreateOrder order, HttpServletRequest request) throws Exception {

        // Check customer
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new ResourceNotFoundException("Customer not authenticated"));
        Customer customer = this.customerRepository.findByEmail(email);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found");
        }

        // Check cart
        Cart cart = this.cartRepository.findByCustomer(customer);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }

        // 3. Validate stock
        for (CartDetail cartDetail : cart.getCartDetails()) {
            Product product = cartDetail.getProduct();
            if (product.getStock() < cartDetail.getQuantity()) {
                throw new StockNotEnoughException("Insufficient stock for product: " + product.getName());
            } else if(product.getStock() - cartDetail.getQuantity() == 0) {
                this.productService.updateStatusProductOutOfStock(product.getId());
            }
        }

        // Create order
        Order orderEntity = new Order();
        orderEntity.setCustomer(customer);
        orderEntity.setOrderCode(this.generateOrderCode());
        orderEntity.setReceiverName(order.getReceiverName());
        orderEntity.setReceiverAddress(order.getReceiverAddress());
        orderEntity.setReceiverPhone(order.getReceiverPhone());
        orderEntity.setNote(order.getNote());
        orderEntity.setStatus(OrderStatus.PENDING);

        List<OrderDetail> details = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartDetail cartDetail : cart.getCartDetails()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(orderEntity);
            orderDetail.setProduct(cartDetail.getProduct());
            orderDetail.setQuantity(cartDetail.getQuantity());
            orderDetail.setPrice(cartDetail.getPrice());
            totalPrice = totalPrice.add(cartDetail.getPrice());
            details.add(orderDetail);
        }
        BigDecimal shipping = BigDecimal.ZERO;
        if (totalPrice.compareTo(new BigDecimal("20000000")) < 0) {
            shipping = new BigDecimal("100000");
            totalPrice = totalPrice.add(shipping); // phải gán lại
        }
        orderEntity.setShipping(shipping);
        orderEntity.setOrderDetails(details);
        orderEntity.setTotalPrice(totalPrice);

        orderEntity = this.orderRepository.save(orderEntity);
        log.info("Order created: {}", orderEntity);

        if ("VNPAY".equalsIgnoreCase(order.getPaymentMethod())) {
            // handle payment vnp
            String ipAddr = VNPayUtil.getIpAddress(request);
            String paymentUrl = this.vnPayService.createPaymentUrl(orderEntity, ipAddr);

            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setPaymentUrl(paymentUrl);
            paymentTransaction.setAmount(totalPrice);
            paymentTransaction.setOrderCode(orderEntity.getOrderCode());
            paymentTransaction.setIpAddress(ipAddr);
            paymentTransaction.setStatus(PaymentStatus.PENDING);
            paymentTransaction.setPaymentMethod(order.getPaymentMethod());
            paymentTransaction.setCurrency("VND");
            paymentTransaction.setOrder(orderEntity);

            orderEntity.setPaymentTransaction(paymentTransaction);
            orderEntity = this.orderRepository.save(orderEntity);
        } else {
            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setOrder(orderEntity);
            paymentTransaction.setAmount(totalPrice);
            paymentTransaction.setOrderCode(orderEntity.getOrderCode());
            paymentTransaction.setStatus(PaymentStatus.PENDING);
            paymentTransaction.setPaymentMethod("COD");
            paymentTransaction.setCurrency("VND");

            orderEntity.setPaymentTransaction(paymentTransaction);
            orderEntity = this.orderRepository.save(orderEntity);
        }
        this.cartDetailRepository.deleteAll(cart.getCartDetails());
        cart.getCartDetails().clear();
        cart.setSum(0);
        return this.convertToResOrderDTO(orderEntity);
    }

    @Override
    public ResPaginationDTO getOrdersWithPagination(Pageable pageable) throws Exception {
        Page<Order> orderPage = this.orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<ResOrderDTO> resOrderPage = orderPage.map(this::convertToResOrderDTO);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(orderPage.getTotalPages());
        meta.setTotal(orderPage.getTotalElements());
        ResPaginationDTO res = new ResPaginationDTO();
        res.setMeta(meta);
        res.setResult(resOrderPage.getContent());
        return res;
    }

    @Override
    @Transactional
    public ResOrderDTO updateOrderInfo(Order order) throws Exception {
        Order orderInDB = this.orderRepository.findById(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() == OrderStatus.PAID && order.getPaymentTransaction() != null
                && order.getPaymentTransaction().getPaymentMethod().equalsIgnoreCase("COD")) {
            orderInDB.setStatus(OrderStatus.PAID);
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Product product = orderDetail.getProduct();
                if (product.getStock() > 0 && product.getStock() >= orderDetail.getQuantity()) {
                    product.setStock(product.getStock() - orderDetail.getQuantity());
                    product.setSold(product.getSold() + orderDetail.getQuantity());
                } else {
                    throw new StockNotEnoughException("Insufficient stock for product: " + product.getName());
                }
                this.productRepository.save(product);
            }
        }
        orderInDB.setStatus(order.getStatus());
        orderInDB = this.orderRepository.save(orderInDB);
        return this.convertToResOrderDTO(orderInDB);
    }

    @Override
    public ResPaginationDTO filterOrders(Pageable pageable, CriteriaFilterOrder criteriaFilterOrder) throws Exception {
        QOrder qOrder = QOrder.order;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteriaFilterOrder.getOrderCode() != null && !criteriaFilterOrder.getOrderCode().isEmpty()) {
            builder.and(qOrder.orderCode.containsIgnoreCase(criteriaFilterOrder.getOrderCode()));
        }

        if (criteriaFilterOrder.getCustomer() != null) {
            builder.and(qOrder.customer.fullName.containsIgnoreCase(criteriaFilterOrder.getCustomer().getFullName()));
        }

        if (criteriaFilterOrder.getStatus() != null && !criteriaFilterOrder.getStatus().toString().isEmpty()) {
            builder.and(qOrder.status.eq(OrderStatus.valueOf(criteriaFilterOrder.getStatus())));
        }

        if (criteriaFilterOrder.getCreatedAt() != null && !criteriaFilterOrder.getCreatedAt().isEmpty()) {
            LocalDate localDate = LocalDate.parse(criteriaFilterOrder.getCreatedAt());
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Instant from = localDate.atStartOfDay(defaultZoneId).toInstant();
            Instant to = localDate.plusDays(1).atStartOfDay(defaultZoneId).minusNanos(1).toInstant();
            builder.and(qOrder.createdAt.between(from, to));
        }

        Page<Order> orderPage = orderRepository.findAll(builder, pageable);
        Page<ResOrderDTO> resOrderPage = orderPage.map(this::convertToResOrderDTO);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(orderPage.getTotalPages());
        meta.setTotal(orderPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(resOrderPage.getContent());
        return res;

    }

    @Override
    public void updateStockAfterPayment(String orderCode) throws Exception {
        Order order = this.findOrderByOrderCodeOrThrow(orderCode);
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Product product = orderDetail.getProduct();
            if (product.getStock() > 0 && product.getStock() >= orderDetail.getQuantity()) {
                product.setStock(product.getStock() - orderDetail.getQuantity());
                product.setSold(product.getSold() + orderDetail.getQuantity());
            } else {
                throw new StockNotEnoughException("Insufficient stock for product: " + product.getName());
            }
            this.productRepository.save(product);
        }
        order.setStatus(OrderStatus.PAID);
        this.orderRepository.save(order);

        try {
            this.emailService.sendInvoiceEmail(order);
            log.info("Invoice email sent for order: {}", orderCode);
        } catch (Exception e) {
            log.error("Failed to send invoice email for order: {}", orderCode, e);
        }
    }

    @Override
    public List<ResMonthlyRevenueDTO> getMonthlyRevenue(Integer year) throws Exception {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<Object[]> results = orderRepository.findMonthlyRevenue(year);
        List<ResMonthlyRevenueDTO> monthlyData = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            String monthStr = String.format("%d-%02d", year, month);
            monthlyData.add(new ResMonthlyRevenueDTO(monthStr, BigDecimal.ZERO));
        }

        Map<String, BigDecimal> revenueMap = results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (BigDecimal) result[1]));

        for (ResMonthlyRevenueDTO data : monthlyData) {
            if (revenueMap.containsKey(data.getMonth())) {
                data.setRevenue(revenueMap.get(data.getMonth()));
            }
        }

        return monthlyData;
    }

    @Override
    public ResStatusOrderAnalyticsDTO getStatusOrderAnalytics() throws Exception {
        List<Object[]> results = orderRepository.countOrderByStatus();
        Map<String, Long> resultMap = results.stream()
                .collect(Collectors.toMap(
                        result -> ((OrderStatus) result[0]).toString(),
                        result -> (Long) result[1]));
        ResStatusOrderAnalyticsDTO res = new ResStatusOrderAnalyticsDTO();
        res.setDelivered(resultMap.getOrDefault(OrderStatus.DELIVERED.toString(), 0L).intValue());
        res.setProcessing(resultMap.getOrDefault(OrderStatus.PROCESSING.toString(), 0L).intValue());
        res.setPending(resultMap.getOrDefault(OrderStatus.PENDING.toString(), 0L).intValue());
        res.setCancelled(resultMap.getOrDefault(OrderStatus.CANCELLED.toString(), 0L).intValue());
        res.setPaid(resultMap.getOrDefault(OrderStatus.PAID.toString(), 0L).intValue());
        res.setShipping(resultMap.getOrDefault(OrderStatus.SHIPPING.toString(), 0L).intValue());
        return res;
    }

}
