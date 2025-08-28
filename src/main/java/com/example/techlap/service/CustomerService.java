package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;

public interface CustomerService {
    // Create a customer
    Customer create(Customer customer) throws Exception;

    // Update a customer
    Customer update(Customer customer) throws Exception;

    // Find a customer by id
    Customer fetchCustomerById(long id) throws Exception;

    // Find a customer by email
    Customer fetchCustomerByEmail(String email);

    // Find all customer with pagination
    ResPaginationDTO fetchAllCustomersWithPagination(Pageable pageable) throws Exception;

    // Delete a customer by id
    void delete(long id) throws Exception;

}
