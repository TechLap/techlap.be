package com.example.techlap.controller;

import com.example.techlap.domain.Category;
import com.example.techlap.domain.annotation.ApiMessage;
import com.example.techlap.domain.respond.DTO.ResCategoryDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.service.CategoryService;
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
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    @ApiMessage("Create a category")
    public ResponseEntity<ResCategoryDTO> createCategory(@Valid @RequestBody Category category) throws Exception {
        Category newCategory = categoryService.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.convertToResCategoryDTO(newCategory));
    }

    @PutMapping("/categories")
    @ApiMessage("Update a category")
    public ResponseEntity<ResCategoryDTO> updateCategory(@Valid @RequestBody Category category) throws Exception {
        Category currentCategory = categoryService.update(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.convertToResCategoryDTO(currentCategory));
    }

    @GetMapping("/categories/{id}")
    @ApiMessage("Fetch category by id")
    public ResponseEntity<ResCategoryDTO> fetchCategoryById(@PathVariable("id") long id) throws Exception {
            Category category = this.categoryService.fetchCategoryById(id);
            return ResponseEntity.status(HttpStatus.OK).body(this.categoryService.convertToResCategoryDTO(category));
    }

    @DeleteMapping("/categories/{id}")
    @ApiMessage("Delete category by id")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") long id) throws Exception {
        this.categoryService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/categories")
    @ApiMessage("Fetch all categories")
    public ResponseEntity<ResPaginationDTO> fetchAllCategories(
            Pageable pageable) throws Exception {
        ResPaginationDTO res = this.categoryService.fetchAllCategoriesWithPagination(pageable);
        return ResponseEntity.ok(res);
    }

}
