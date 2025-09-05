package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Brand;
import com.example.techlap.domain.respond.DTO.ResBrandDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;

public interface BrandService {

    // Create a brand
    Brand create(Brand brand) throws Exception;

    // Update a brand
    Brand update(Brand brand) throws Exception;

    // Find a brand by id
    Brand fetchBrandById(long id) throws Exception;

    // Find a brand by name
    Brand fetchBrandByName(String name);

    // Find all brand with pagination
    ResPaginationDTO fetchAllBrandsWithPagination(Pageable pageable) throws Exception;

    // Delete a brand by id
    void delete(long id) throws Exception;

    ResBrandDTO convertToResBrandDTO(Brand brand);

}
