package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Product;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResProductDTO;

public interface ProductService {

    // Create a product
    Product create(Product product) throws Exception;

    // Update a product
    Product update(Product product) throws Exception;

    // Find a product by id
    Product fetchProductById(long id) throws Exception;

    // Find a product by name
    Product fetchProductByName(String name);

    // Find all product with pagination
    ResPaginationDTO fetchAllProductsWithPagination(Pageable pageable) throws Exception;

    // Delete a product by id
    void delete(long id) throws Exception;

    ResProductDTO convertToResProductDTO(Product product) throws Exception;

}
