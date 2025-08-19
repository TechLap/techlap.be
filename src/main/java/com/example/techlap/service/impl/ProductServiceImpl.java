package com.example.techlap.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.techlap.domain.Product;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.ProductRepository;
import com.example.techlap.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private static final String PRODUCT_EXISTS_EXCEPTION_MESSAGE = "Product already exists";
    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Product not found";

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private Product findProductByIdOrThrow(long id) {
        return this.productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public Product create(Product product) throws Exception {
        // Check if product already exists
        if (this.productRepository.existsById(product.getId())) {
            throw new ResourceNotFoundException(PRODUCT_EXISTS_EXCEPTION_MESSAGE);
        }
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

        return res;
    }

    @Override
    public void delete(long id) throws Exception {
        Product product = this.findProductByIdOrThrow(id);
        this.productRepository.delete(product);
    }
}
