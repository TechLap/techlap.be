package com.example.techlap.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.techlap.domain.Brand;
import com.example.techlap.domain.respond.DTO.ResBrandDTO;
import com.example.techlap.domain.respond.DTO.ResPaginationDTO;
import com.example.techlap.exception.ResourceNotFoundException;
import com.example.techlap.repository.BrandRepository;
import com.example.techlap.service.BrandService;
import com.example.techlap.service.ProductService;

import ch.qos.logback.core.model.Model;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private static final String BRAND_EXISTS_EXCEPTION_MESSAGE = "Brand already exists";
    private static final String BRAND_NOT_FOUND_EXCEPTION_MESSAGE = "Brand not found";

    private Brand findBrandByIdOrThrow(long id) {
        return this.brandRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BRAND_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public ResBrandDTO convertToResBrandDTO(Brand brand) {
        return modelMapper.map(brand, ResBrandDTO.class);
    }

    @Override
    public Brand create(Brand brand) throws Exception {
        // Check if brand already exists
        if (this.brandRepository.existsByName(brand.getName())) {
            throw new ResourceNotFoundException(BRAND_EXISTS_EXCEPTION_MESSAGE);
        }
        return brandRepository.save(brand);
    }

    @Override
    public Brand update(Brand brand) throws Exception {
        Brand brandInDB = this.findBrandByIdOrThrow(brand.getId());

        brandInDB.setName(brand.getName());
        brandInDB.setProducts(brand.getProducts());

        return brandRepository.save(brandInDB);
    }

    @Override
    public Brand fetchBrandById(long id) throws Exception {

        return this.findBrandByIdOrThrow(id);
    }

    @Override
    public Brand fetchBrandByName(String name) {
        return brandRepository.findByName(name);
    }

    @Override
    public ResPaginationDTO fetchAllBrandsWithPagination(Pageable pageable) throws Exception {
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        ResPaginationDTO res = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();

        meta.setPage(brandPage.getNumber() + 1);
        meta.setPageSize(brandPage.getSize());
        meta.setPages(brandPage.getTotalPages());
        meta.setTotal(brandPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(brandPage.getContent());

        List<ResBrandDTO> listBrands = brandPage.getContent().stream()
                .map(this::convertToResBrandDTO)
                .toList();
        res.setResult(listBrands);

        return res;
    }

    @Override
    public void delete(long id) throws Exception {
        Brand brand = this.findBrandByIdOrThrow(id);
        this.brandRepository.delete(brand);
    }
}
