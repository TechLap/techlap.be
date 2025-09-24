package com.example.techlap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import com.example.techlap.domain.enums.OrderStatus;
import com.example.techlap.domain.enums.PaymentStatus;
import com.example.techlap.domain.request.ReqCreateOrder;
import com.example.techlap.domain.respond.DTO.ResOrderDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO.Meta;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.CartDetailRepository;
import com.example.techlap.repository.CartRepository;
import com.example.techlap.repository.CustomerRepository;
import com.example.techlap.repository.OrderRepository;
import com.example.techlap.service.OrderService;
import com.example.techlap.util.SecurityUtil;
import com.example.techlap.service.VNPayService;
import com.example.techlap.util.payment.VNPayUtil;

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

    // private ResOrderDetailDTO convertToResOrderDetailDTO(OrderDetail orderDetail)
    // {
    // return this.modelMapper.map(orderDetail, ResOrderDetailDTO.class);
    // }

    private ResOrderDTO convertToResOrderDTO(Order order) {
        ResOrderDTO dto = this.modelMapper.map(order, ResOrderDTO.class);
        return dto;
    }

    private String generateOrderCode() {
        String random = VNPayUtil.getRandomNumber(8);
        return "TLS-" + random;
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) throws Exception {
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        this.orderRepository.save(order);
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
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
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

}
