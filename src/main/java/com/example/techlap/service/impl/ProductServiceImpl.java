package com.example.techlap.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.techlap.config.ModelMapperConfig;
import com.example.techlap.domain.Brand;
import com.example.techlap.domain.Category;
import com.example.techlap.domain.Product;
import com.example.techlap.domain.QProduct;
import com.example.techlap.domain.criteria.CriteriaFilterProduct;
import com.example.techlap.domain.enums.ProductStatus;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.domain.respond.DTO.ResProductDTO;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.util.ForeignKeyConstraintHandler;
import com.example.techlap.repository.ProductRepository;
import com.example.techlap.service.BrandService;
import com.example.techlap.service.CategoryService;
import com.example.techlap.service.ProductService;
import com.querydsl.core.BooleanBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private static final String PRODUCT_EXISTS_EXCEPTION_MESSAGE = "Product already exists";
    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Product not found";

    private Product findProductByIdOrThrow(long id) {
        return this.productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public ResProductDTO convertToResProductDTO(Product product) {
        return modelMapper.map(product, ResProductDTO.class);
    }

    @Override
    public Product create(Product product) throws Exception {
        // Check if product already exists
        if (this.productRepository.existsByName(product.getName())) {
            throw new ResourceNotFoundException(PRODUCT_EXISTS_EXCEPTION_MESSAGE);
        }

        // Check brand
        if (product.getBrand() != null) {
            Brand brand = this.brandService.fetchBrandById(product.getBrand().getId());
            product.setBrand(brand != null ? brand : null);
        }

        // Check category
        if (product.getCategory() != null) {
            Category category = this.categoryService.fetchCategoryById(product.getCategory().getId());
            product.setCategory(category != null ? category : null);
        }

        product.setSold(0);
        return productRepository.save(product);
    }

    @Override
    public Product update(Product product) throws Exception {
        Product productInDB = this.findProductByIdOrThrow(product.getId());

        productInDB.setName(product.getName());
        productInDB.setDescription(product.getDescription());
        productInDB.setPrice(product.getPrice());
        productInDB.setCategory(product.getCategory());
        productInDB.setImage(product.getImage());
        productInDB.setStock(product.getStock());
        productInDB.setStatus(product.getStatus());
        productInDB.setBrand(product.getBrand());
        productInDB.setDiscount(product.getDiscount());

        return productRepository.save(productInDB);
    }

    @Override
    public Product fetchProductById(long id) throws Exception {

        return this.findProductByIdOrThrow(id);
    }

    @Override
    public Product fetchProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public ResPaginationDTO fetchAllProductsWithPagination(Pageable pageable) throws Exception {
        Page<Product> prodPage = productRepository.findAll(pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(prodPage.getNumber() + 1);
        meta.setPageSize(prodPage.getSize());
        meta.setPages(prodPage.getTotalPages());
        meta.setTotal(prodPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(prodPage.getContent());

        List<ResProductDTO> listProd = prodPage.getContent().stream()
                .map(this::convertToResProductDTO)
                .toList();

        res.setResult(listProd);
        return res;
    }

    @Override
    @Transactional
    public void delete(long id) throws Exception {
        Product product = this.findProductByIdOrThrow(id);
        
        ForeignKeyConstraintHandler.handleDeleteWithForeignKeyCheck(
            () -> this.productRepository.delete(product),
            "sản phẩm",
            "đơn hàng"
        );
    }

    @Override
    public ResPaginationDTO filterProducts(Pageable pageable, CriteriaFilterProduct criteriaFilterProduct)
            throws Exception {

        QProduct qProduct = QProduct.product;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteriaFilterProduct.getName() != null && !criteriaFilterProduct.getName().isEmpty()) {
            builder.and(qProduct.name.containsIgnoreCase(criteriaFilterProduct.getName()));
        }
        if (criteriaFilterProduct.getStatus() != null && !criteriaFilterProduct.getStatus().toString().isEmpty()) {
            builder.and(qProduct.status.eq(ProductStatus.valueOf(criteriaFilterProduct.getStatus())));
        }

        if (criteriaFilterProduct.getBrand() != null && criteriaFilterProduct.getBrand().getName() != null && !criteriaFilterProduct.getBrand().getName().isEmpty()) {
            builder.and(qProduct.brand.name.containsIgnoreCase(criteriaFilterProduct.getBrand().getName()));
        }

        if (criteriaFilterProduct.getCategory() != null) {
            builder.and(qProduct.category.eq(criteriaFilterProduct.getCategory()));
        }
        if (criteriaFilterProduct.getPriceRange() != null
                && (criteriaFilterProduct.getPriceRange().getMin() != null
                        || criteriaFilterProduct.getPriceRange().getMax() != null)) {
            if (criteriaFilterProduct.getPriceRange().getMin() != null) {
                builder.and(qProduct.price.goe(criteriaFilterProduct.getPriceRange().getMin()));
            }
            if (criteriaFilterProduct.getPriceRange().getMax() != null) {
                builder.and(qProduct.price.loe(criteriaFilterProduct.getPriceRange().getMax()));
            }
        }
        if (criteriaFilterProduct.getCreatedAt() != null && !criteriaFilterProduct.getCreatedAt().isEmpty()) {
            LocalDate localDate = LocalDate.parse(criteriaFilterProduct.getCreatedAt());
            ZoneId zone = ZoneId.systemDefault();
            Instant from = localDate.atStartOfDay(zone).toInstant();
            Instant to = localDate.plusDays(1).atStartOfDay(zone).minusNanos(1).toInstant();
            builder.and(qProduct.createdAt.between(from, to));
        }

        Page<Product> prodPage = productRepository.findAll(builder, pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(prodPage.getNumber() + 1);
        meta.setPageSize(prodPage.getSize());
        meta.setPages(prodPage.getTotalPages());
        meta.setTotal(prodPage.getTotalElements());

        res.setMeta(meta);
        List<ResProductDTO> listProd = prodPage.getContent().stream()
                .map(this::convertToResProductDTO)
                .toList();

        res.setResult(listProd);
        return res;
    }

    @Override
    public List<Product> fetchAllLatestProducts() throws Exception {
        return this.productRepository.findTop5ByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE);
    }
    @Override
    public List<Product> fetchAllBestSellingProducts() throws Exception {
        return this.productRepository.findTop5BestSellingProducts();
    }

    @Override
    public void updateStatusProductOutOfStock(long id) throws Exception {
        Product product = this.findProductByIdOrThrow(id);
        product.setStatus(ProductStatus.OUT_OF_STOCK);
        this.productRepository.save(product);
    }
}
