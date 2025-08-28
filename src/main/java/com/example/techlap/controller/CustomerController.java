package com.example.techlap.controller;

import com.example.techlap.domain.Customer;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.CustomerService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    @ApiMessage("Create a customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws Exception {
        Customer newCustomer = customerService.create(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @PutMapping("/customers")
    @ApiMessage("Update a customer")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody Customer customer) throws Exception {
        Customer currentCustomer = customerService.update(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(currentCustomer);
    }

    @GetMapping("/customers/{id}")
    @ApiMessage("Fetch customer by id")
    public ResponseEntity<Customer> fetchCustomerById(@PathVariable("id") long id) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.customerService.fetchCustomerById(id));
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

}
