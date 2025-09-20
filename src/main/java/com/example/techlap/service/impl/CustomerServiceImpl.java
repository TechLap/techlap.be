package com.example.techlap.service.impl;

import com.example.techlap.domain.*;
import com.example.techlap.domain.criteria.CriteriaFilterCustomer;
import com.example.techlap.domain.request.ReqAddToCartDTO;
import com.example.techlap.domain.request.ReqUpdateCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResCartDTO;
import com.example.techlap.domain.respond.DTO.ResCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.IdInvalidException;
import com.example.techlap.domain.request.ReqChangePasswordDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.*;
import com.example.techlap.service.CustomerService;
import com.example.techlap.util.SecurityUtil;
import com.querydsl.core.BooleanBuilder;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ModelMapper modelMapper;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE = "Customer not found";
    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Product not found";

    private Customer findCustomerByIdOrThrow(long id) {
        return this.customerRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public ResCustomerDTO convertToResCustomerDTO(Customer customer) {
        return modelMapper.map(customer, ResCustomerDTO.class);
    }

    @Override
    public ResCartDTO convertToResCartDTO(Cart cart) {
        ResCartDTO dto = new ResCartDTO();
        dto.setId(cart.getId());
        dto.setSum(cart.getSum());

        // Map customer
        ResCustomerDTO customerDTO = modelMapper.map(cart.getCustomer(), ResCustomerDTO.class);
        dto.setCustomer(customerDTO);

        // Map cart details
        List<ResCartDTO.CartDetailDTO> detailDTOs = cart.getCartDetails().stream()
                .map(detail -> {
                    ResCartDTO.CartDetailDTO d = new ResCartDTO.CartDetailDTO();
                    d.setId(detail.getId());
                    d.setQuantity(detail.getQuantity());
                    d.setPrice(detail.getPrice());

                    // Map product
                    ResCartDTO.ProductDTO p = modelMapper.map(detail.getProduct(), ResCartDTO.ProductDTO.class);
                    d.setProduct(p);

                    return d;
                })
                .toList();

        dto.setCartDetails(detailDTOs);
        return dto;
    }

    @Override
    public Customer create(Customer customer) throws Exception {
        // Check Customername
        if (this.customerRepository.existsByEmail(customer.getEmail())
                || this.userRepository.existsByEmail(customer.getEmail()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);
        Role customerRole = roleRepository.findByName("CUSTOMER");
        if (customerRole == null) {
            throw new ResourceNotFoundException("Role CUSTOMER not found");
        }
        customer.setRole(customerRole);
        // Save hashPassword
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        return customerRepository.save(customer);
    }

    @Override
    public Customer update(ReqUpdateCustomerDTO reqCustomer) throws Exception {
        Customer customerInDB = this.findCustomerByIdOrThrow(reqCustomer.getId());

        customerInDB.setFullName(reqCustomer.getFullName());
        customerInDB.setPhone(reqCustomer.getPhone());
        customerInDB.setAddress(reqCustomer.getAddress());
        return this.customerRepository.save(customerInDB);
    }

    @Override
    public Customer fetchCustomerById(long id) throws Exception {
        return this.findCustomerByIdOrThrow(id);
    }

    @Override
    public Customer fetchCustomerByEmail(String email) {
        return this.customerRepository
                .findByEmail(email);
    }

    @Override
    public void delete(long id) throws Exception {
        Customer customer = this.findCustomerByIdOrThrow(id);
        this.customerRepository.delete(customer);
    }

    @Override
    public void updateCustomerToken(String token, String email) throws Exception {
        Customer currentCustomer = this.fetchCustomerByEmail(email);
        if (currentCustomer != null) {
            currentCustomer.setRefreshToken(token);
            this.customerRepository.save(currentCustomer);
        }
    }

    @Override
    public Customer getCustomerByRefreshTokenAndEmail(String token, String email) throws Exception {
        return this.customerRepository.findByRefreshTokenAndEmail(token, email);
    }

    @Override
    public ResPaginationDTO fetchAllCustomersWithPagination(Pageable pageable) throws Exception {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(customerPage.getNumber() + 1);
        meta.setPageSize(customerPage.getSize());
        meta.setPages(customerPage.getTotalPages());
        meta.setTotal(customerPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(customerPage.getContent());

        List<ResCustomerDTO> customerDTOs = customerPage.getContent()
                .stream()
                .map(this::convertToResCustomerDTO)
                .toList();

        res.setResult(customerDTOs);

        return res;
    }

    @Override
    public ResPaginationDTO filterCustomers(Pageable pageable, CriteriaFilterCustomer criteriaFilterCustomer)
            throws Exception {
        QCustomer qCustomer = QCustomer.customer;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteriaFilterCustomer.getEmail() != null && !criteriaFilterCustomer.getEmail().isEmpty()) {
            builder.and(qCustomer.email.containsIgnoreCase(criteriaFilterCustomer.getEmail()));
        }
        if (criteriaFilterCustomer.getFullName() != null && !criteriaFilterCustomer.getFullName().isEmpty()) {
            builder.and(qCustomer.fullName.containsIgnoreCase(criteriaFilterCustomer.getFullName()));
        }
        if (criteriaFilterCustomer.getPhone() != null && !criteriaFilterCustomer.getPhone().isEmpty()) {
            builder.and(qCustomer.phone.containsIgnoreCase(criteriaFilterCustomer.getPhone()));
        }
        if (criteriaFilterCustomer.getAddress() != null && !criteriaFilterCustomer.getAddress().isEmpty()) {
            builder.and(qCustomer.address.containsIgnoreCase(criteriaFilterCustomer.getAddress()));
        }
        if (criteriaFilterCustomer.getCreatedAt() != null && !criteriaFilterCustomer.getCreatedAt().isEmpty()) {
            LocalDate localDate = LocalDate.parse(criteriaFilterCustomer.getCreatedAt());
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Instant from = localDate.atStartOfDay(defaultZoneId).toInstant();
            Instant to = localDate.plusDays(1).atStartOfDay(defaultZoneId).minusNanos(1).toInstant();
            builder.and(qCustomer.createdAt.between(from, to));
        }

        Page<Customer> customerPage = customerRepository.findAll(builder, pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(customerPage.getNumber() + 1);
        meta.setPageSize(customerPage.getSize());
        meta.setPages(customerPage.getTotalPages());
        meta.setTotal(customerPage.getTotalElements());

        res.setMeta(meta);

        List<ResCustomerDTO> customerDTOs = customerPage.getContent()
                .stream()
                .map(this::convertToResCustomerDTO)
                .toList();
        res.setResult(customerDTOs);
        return res;
    }

    private Cart createOrGetCartForCustomer(Customer customer) {
        if (customer.getCart() == null) {
            Cart cart = new Cart();
            cart.setCustomer(customer);
            cart.setSum(0);
            cart = this.cartRepository.save(cart);
            return cart;
        } else {
            return customer.getCart();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Cart addToCart (ReqAddToCartDTO reqAddToCartDTO) throws Exception {
        // Lay thong tin cua customer ra kiem tra xem co gio hang chua?
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";
        Customer currentCustomerDB = this.fetchCustomerByEmail(email);

        Cart cart = createOrGetCartForCustomer(currentCustomerDB);

        // Kiem tra product_id co phu hop hay la khong
        Product product = productRepository.findById(reqAddToCartDTO.getProductId())
                .orElseThrow(() -> new IdInvalidException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));

        // Kiem tra product co nam trong cart chua
        CartDetail cartDetail = cartDetailRepository.findByCartAndProduct(cart, product);

        if (cartDetail == null) {
            // Nếu chưa có thì chỉ cho phép thêm mới (isUpdate = false)
            if (reqAddToCartDTO.isUpdate()) {
                throw new Exception("Product not found in cart");
            }
            cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProduct(product);
            cartDetail.setQuantity(reqAddToCartDTO.getQuantity());
        } else {
            // Nếu đã có
            if (reqAddToCartDTO.isUpdate()) {
                cartDetail.setQuantity(reqAddToCartDTO.getQuantity()); // Set trực tiếp
            } else {
                cartDetail.setQuantity(cartDetail.getQuantity() + reqAddToCartDTO.getQuantity()); // Cộng thêm
            }
        }

        // Tính đơn giá sau giảm
        BigDecimal price = product.getPrice();
        BigDecimal discount = BigDecimal.valueOf(product.getDiscount())
                .divide(BigDecimal.valueOf(100));
        BigDecimal finalPrice = price.subtract(price.multiply(discount));
        BigDecimal totalPrice = finalPrice.multiply(BigDecimal.valueOf(cartDetail.getQuantity()));
        cartDetail.setPrice(totalPrice);

        this.cartDetailRepository.save(cartDetail);

        // Cap nhat so luong
        int totalItems = cartDetailRepository.countByCart(cart);
        cart.setSum(totalItems);
        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Cart getCartByEmail(String email) throws Exception {
        // return this.cartRepository.findBy()
        return null;
    }

    @Override
    public void changePassword(Long id, ReqChangePasswordDTO password) throws Exception {
        Customer customerInDB = this.findCustomerByIdOrThrow(id);

        if (checkIfValidOldPassword(customerInDB, password.getOldPassword())) {
            if (password.getNewPassword().equals(password.getReNewPassword())) {
                customerInDB.setPassword(passwordEncoder.encode(password.getNewPassword()));
                customerRepository.save(customerInDB);
            } else {
                throw new IllegalArgumentException("New password and re-new password do not match");
            }
        } else {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    }

    @Override
    public boolean checkIfValidOldPassword(Customer customer, String oldPassword) {
        return passwordEncoder.matches(oldPassword, customer.getPassword());
    }

    @Override
    public void changeCustomerPassword(Customer customer, String password) {
        customer.setPassword(passwordEncoder.encode(password));
        customerRepository.save(customer);
        passwordResetTokenRepository.deleteByCustomer(customer);
        passwordResetTokenRepository.flush();
    }

    @Override
    public Customer getCustomerByPasswordResetToken(String token) throws Exception {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        Customer customer = passwordResetToken.getCustomer();
        return customer;
    }

    @Override
    public Cart getCartByCustomer() throws Exception {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";
        Customer currentCustomerDB = this.fetchCustomerByEmail(email);
        return createOrGetCartForCustomer(currentCustomerDB);
    }

    @Override
    @Transactional (rollbackFor = Exception.class)
    public void removeCartDetailForCart(long cartDetailId, long customerId) throws Exception {
        // 1. Tìm customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new Exception("Customer not found"));

        // 2. Lấy cart của customer
        Cart cart = cartRepository.findByCustomer(customer);
        if (cart == null) {
            throw new Exception("Cart not found for this customer");
        }

        // 3. Lấy cartDetail trong cart
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new Exception("CartDetail not found"));

        // 4. Kiểm tra cartDetail có thuộc cart này không
        if (cartDetail.getCart().getId() != (cart.getId())) {
            throw new Exception("CartDetail does not belong to this customer's cart");
        }

        // 5. Thực hiện xóa
        cartDetailRepository.delete(cartDetail);

        // 6. Cập nhật lại tổng số lượng trong cart (nếu có field sum)
        int totalItems = cartDetailRepository.countByCart(cart);
        cart.setSum(totalItems);
        cartRepository.save(cart);
    }
}