package com.example.techlap.service;

import com.example.techlap.domain.Cart;
import com.example.techlap.domain.request.ReqAddToCartDTO;
import com.example.techlap.domain.request.ReqAdminChangePasswordDTO;
import com.example.techlap.domain.request.ReqChangePasswordDTO;
import com.example.techlap.domain.respond.DTO.ResCartDTO;
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

    // Update a token
    void updateCustomerToken(String token, String email) throws Exception;

    Customer getCustomerByRefreshTokenAndEmail(String token, String email) throws Exception;

    // Convert entity to DTO
    ResCustomerDTO convertToResCustomerDTO(Customer customer);

    ResCartDTO convertToResCartDTO(Cart cart);

    ResPaginationDTO filterCustomers(Pageable pageable, CriteriaFilterCustomer criteriaFilterCustomer) throws Exception;

    Cart addToCart(ReqAddToCartDTO reqAddToCartDTO) throws Exception;

    Cart getCartByEmail(String email) throws Exception;

    void adminChangePassword(long id, ReqAdminChangePasswordDTO changePasswordDTO) throws Exception;

    boolean checkIfValidOldPassword(Customer customer, String oldPassword);

    void changeCustomerPassword(Customer customer, String newPassword);

    Customer getCustomerByPasswordResetToken(String token) throws Exception;

    Cart getCartByCustomer() throws Exception;

    void removeCartDetailForCart(long cartDetailId, long customerId) throws Exception;
    void changePasswordByEmail(String email, ReqChangePasswordDTO dto) throws Exception;
}
