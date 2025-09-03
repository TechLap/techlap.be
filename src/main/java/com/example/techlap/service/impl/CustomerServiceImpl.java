package com.example.techlap.service.impl;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.CustomerRepository;
import com.example.techlap.service.CustomerService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE = "Customer not found";


    private Customer findCustomerByIdOrThrow(long id) {
        return this.customerRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE));
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
    public Customer update(Customer customer) throws Exception {
        Customer customerInDB = this.findCustomerByIdOrThrow(customer.getId());

        customerInDB.setFullName(customer.getFullName());
        customerInDB.setPhone(customer.getPhone());

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

        return res;
    }
}
