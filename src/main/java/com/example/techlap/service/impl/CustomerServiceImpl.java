package com.example.techlap.service.impl;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.QCustomer;
import com.example.techlap.domain.criteria.CriteriaFilterCustomer;
import com.example.techlap.domain.request.ReqUpdateCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.CustomerRepository;
import com.example.techlap.service.CustomerService;
import com.querydsl.core.BooleanBuilder;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE = "Customer not found";

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
    public Customer create(Customer customer) throws Exception {
        // Check Customername
        if (this.customerRepository.existsByEmail(customer.getEmail()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);

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
}