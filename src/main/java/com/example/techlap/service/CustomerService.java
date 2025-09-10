package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.criteria.CriteriaFilterCustomer;
import com.example.techlap.domain.request.ReqUpdateCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;

public interface CustomerService {
    // Create a customer
    Customer create(Customer customer) throws Exception;

    // Update a customer
    Customer update(ReqUpdateCustomerDTO reqCustomer) throws Exception;

    // Find a customer by id
    Customer fetchCustomerById(long id) throws Exception;

    // Find a customer by email
    Customer fetchCustomerByEmail(String email);

    // Find all customer with pagination
    ResPaginationDTO fetchAllCustomersWithPagination(Pageable pageable) throws Exception;

    // Delete a customer by id
    void delete(long id) throws Exception;

    // Convert entity to DTO
    ResCustomerDTO convertToResCustomerDTO(Customer customer);

    ResPaginationDTO filterCustomers (Pageable pageable, CriteriaFilterCustomer criteriaFilterCustomer) throws Exception;
}
