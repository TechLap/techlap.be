package com.example.techlap.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.techlap.domain.Brand;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.BrandService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/brands")
    @ApiMessage("Create a brand")
    public ResponseEntity<Brand> createUser(@Valid @RequestBody Brand brand) throws Exception {
        Brand newBrand = brandService.create(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
    }

    @PutMapping("/brands")
    @ApiMessage("Update a brand")
    public ResponseEntity<Brand> updateUser(@Valid @RequestBody Brand brand) throws Exception {
        Brand currentProd = brandService.update(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(currentProd);
    }

    @GetMapping("/brands/{id}")
    @ApiMessage("Fetch brand by id")
    public ResponseEntity<Brand> fetchProductById(@PathVariable("id") long id) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.brandService.fetchProductById(id));
    }

    @GetMapping("/brands")
    @ApiMessage("Fetch all brands")
    public ResponseEntity<ResPaginationDTO> fetchAllProducts(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.brandService.fetchAllProductsWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/brands/{id}")
    @ApiMessage("Delete brand by id")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) throws Exception {
        this.brandService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
