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
import com.example.techlap.domain.respond.DTO.ResBrandDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.BrandService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class BrandController {

    private final BrandService brandService;

    @PostMapping("/brands")
    @ApiMessage("Create a brand")
    public ResponseEntity<ResBrandDTO> createUser(@Valid @RequestBody Brand brand) throws Exception {
        Brand newBrand = brandService.create(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.brandService.convertToResBrandDTO(newBrand));
    }

    @PutMapping("/brands")
    @ApiMessage("Update a brand")
    public ResponseEntity<ResBrandDTO> updateUser(@Valid @RequestBody Brand brand) throws Exception {
        Brand currentProd = brandService.update(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.brandService.convertToResBrandDTO(currentProd));
    }

    @GetMapping("/brands/{id}")
    @ApiMessage("Fetch brand by id")
    public ResponseEntity<ResBrandDTO> fetchProductById(@PathVariable("id") long id) throws Exception {
        Brand brand = this.brandService.fetchBrandById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.brandService.convertToResBrandDTO(brand));
    }

    @GetMapping("/brands")
    @ApiMessage("Fetch all brands")
    public ResponseEntity<ResPaginationDTO> fetchAllProducts(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.brandService.fetchAllBrandsWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/brands/{id}")
    @ApiMessage("Delete brand by id")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) throws Exception {
        this.brandService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
