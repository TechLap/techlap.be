package com.example.techlap.service.impl;

import com.example.techlap.domain.Category;
import com.example.techlap.domain.QCategory;
import com.example.techlap.domain.criteria.CriteriaFilterCategory;
import com.example.techlap.domain.respond.DTO.ResCategoryDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.ResourceAlreadyExistsException;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.util.ForeignKeyConstraintHandler;
import com.example.techlap.repository.CategoryRepository;
import com.example.techlap.service.CategoryService;
import com.querydsl.core.BooleanBuilder;

import lombok.AllArgsConstructor;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private static final String NAME_EXISTS_EXCEPTION_MESSAGE = "Name already exists";
    private static final String CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE = "Category not found";

    private Category findCategoryByIdOrThrow(long id) {
        return this.categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public ResCategoryDTO convertToResCategoryDTO(Category category) {
        return modelMapper.map(category, ResCategoryDTO.class);
    }

    @Override
    public Category create(Category category) throws Exception {
        if (this.categoryRepository.existsByName(category.getName()))
            throw new ResourceAlreadyExistsException(NAME_EXISTS_EXCEPTION_MESSAGE);

        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category) throws Exception {
        Category userInDB = this.findCategoryByIdOrThrow(category.getId());

        userInDB.setName(category.getName());
        userInDB.setDescription(category.getDescription());

        return this.categoryRepository.save(userInDB);
    }

    @Override
    public Category fetchCategoryById(long id) throws Exception {
        return this.findCategoryByIdOrThrow(id);
    }

    @Override
    public Category fetchCategoryByName(String name) {
        return this.categoryRepository
                .findByName(name);
    }

    @Override
    @Transactional
    public void delete(long id) throws Exception {
        Category category = this.findCategoryByIdOrThrow(id);
        
        ForeignKeyConstraintHandler.handleDeleteWithForeignKeyCheck(
            () -> this.categoryRepository.delete(category),
            "danh mục",
            "sản phẩm"
        );
    }

    @Override
    public ResPaginationDTO fetchAllCategoriesWithPagination(Pageable pageable) throws Exception {
        Page<Category> userPage = categoryRepository.findAll(pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(userPage.getNumber() + 1);
        meta.setPageSize(userPage.getSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(userPage.getContent());

        List<ResCategoryDTO> categoryDTOs = userPage.getContent()
                .stream()
                .map(this::convertToResCategoryDTO)
                .toList();
        res.setResult(categoryDTOs);
        return res;
    }

    @Override
    public ResPaginationDTO filterCategories(Pageable pageable, CriteriaFilterCategory criteriaFilterCategory) {
        QCategory qCategory = QCategory.category;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteriaFilterCategory.getName() != null && !criteriaFilterCategory.getName().isEmpty()) {
            builder.and(qCategory.name.containsIgnoreCase(criteriaFilterCategory.getName()));
        }

        Page<Category> catPage = categoryRepository.findAll(builder, pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(catPage.getNumber() + 1);
        meta.setPageSize(catPage.getSize());
        meta.setPages(catPage.getTotalPages());
        meta.setTotal(catPage.getTotalElements());

        res.setMeta(meta);

        List<ResCategoryDTO> listCat = catPage.getContent()
                .stream()
                .map(this::convertToResCategoryDTO)
                .toList();

        res.setResult(listCat);
        return res;
    }

}
