package com.example.techlap.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.techlap.domain.Product;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.criteria.CriteriaFilterProduct;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResProductDTO;
import com.example.techlap.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    @ApiMessage("Create a product")
    public ResponseEntity<ResProductDTO> createUser(@Valid @RequestBody Product product) throws Exception {
        Product newProd = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.convertToResProductDTO(newProd));
    }

    @PutMapping("/products")
    @ApiMessage("Update a product")
    public ResponseEntity<ResProductDTO> updateUser(@Valid @RequestBody Product product) throws Exception {
        Product currentProd = productService.update(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.convertToResProductDTO(currentProd));
    }

    @GetMapping("/products/{id}")
    @ApiMessage("Fetch product by id")
    public ResponseEntity<ResProductDTO> fetchProductById(@PathVariable("id") long id) throws Exception {
        Product prod = this.productService.fetchProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.convertToResProductDTO(prod));
    }

    @GetMapping("/products")
    @ApiMessage("Fetch all products")
    public ResponseEntity<ResPaginationDTO> fetchAllProducts(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.productService.fetchAllProductsWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/products/{id}")
    @ApiMessage("Delete product by id")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) throws Exception {
        this.productService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("products/filter")
    @ApiMessage("Filter products")
    public ResponseEntity<ResPaginationDTO> filterProducts(Pageable pageable,
            @RequestBody CriteriaFilterProduct criteriaFilterProduct) throws Exception {
        ResPaginationDTO res = this.productService.filterProducts(pageable, criteriaFilterProduct);
        return ResponseEntity.ok(res);
    }

}
