package com.example.techlap.service;

import org.springframework.data.domain.Pageable;

import com.example.techlap.domain.Category;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;

public interface CategoryService {
    // Create a category
    Category create(Category category) throws Exception;

    // Update a category
    Category update(Category category) throws Exception;

    // Find a category by id
    Category fetchCategoryById(long id) throws Exception;

    // Find a category by username
    Category fetchCategoryByName(String name);

    // Find all category with pagination
    ResPaginationDTO fetchAllCategorysWithPagination(Pageable pageable) throws Exception;

    // Delete a category by id
    void delete(long id) throws Exception;

}
