package com.example.techlap.controller;

import com.example.techlap.domain.Cart;
import com.example.techlap.domain.CartDetail;
import com.example.techlap.domain.Customer;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.criteria.CriteriaFilterCustomer;
import com.example.techlap.domain.request.ReqAddToCartDTO;
import com.example.techlap.domain.request.ReqUpdateCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResCartDTO;
import com.example.techlap.domain.respond.DTO.ResCustomerDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/customers")
    @ApiMessage("Create a customer")
    public ResponseEntity<ResCustomerDTO> createCustomer(@Valid @RequestBody Customer customer) throws Exception {
        Customer newCustomer = customerService.create(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.convertToResCustomerDTO(newCustomer));
    }

    @PutMapping("/customers")
    @ApiMessage("Update a customer")
    public ResponseEntity<ResCustomerDTO> updateCustomer(@Valid @RequestBody ReqUpdateCustomerDTO reqCustomer)
            throws Exception {
        Customer currentCustomer = customerService.update(reqCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.convertToResCustomerDTO(currentCustomer));
    }

    @GetMapping("/customers/{id}")
    @ApiMessage("Fetch customer by id")
    public ResponseEntity<ResCustomerDTO> fetchCustomerById(@PathVariable("id") long id) throws Exception {
        ResCustomerDTO customerDTO = this.customerService
                .convertToResCustomerDTO(this.customerService.fetchCustomerById(id));
        return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    @ApiMessage("Delete customer by id")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") long id) throws Exception {
        this.customerService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/customers")
    @ApiMessage("Fetch all customers")
    public ResponseEntity<ResPaginationDTO> fetchAllCustomers(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.customerService.fetchAllCustomersWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/customers/filter")
    @ApiMessage("Filter customers")
    public ResponseEntity<ResPaginationDTO> filterCustomers(
            Pageable pageable,
            @RequestBody CriteriaFilterCustomer criteriaFilterCustomer) throws Exception {
        ResPaginationDTO res = this.customerService.filterCustomers(pageable, criteriaFilterCustomer);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/customers/add-to-cart")
    @ApiMessage("Add a product into cart")
    public ResponseEntity<ResCartDTO> addToCart(@RequestBody ReqAddToCartDTO reqAddToCartDTO) throws Exception {
        Cart cart = this.customerService.addToCart(reqAddToCartDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.customerService.convertToResCartDTO(cart));
    }

    @GetMapping("/customers/get-cart")
    @ApiMessage("Get a cart")
    public ResponseEntity<ResCartDTO> getCart(@RequestBody String email) throws Exception {
        return null;
    }
}
