package com.example.techlap.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "name isn't blank")
    private String name;
    private double price;
    private double discount;
    private long stock;
    @NotBlank(message = "description isn't blank")
    private String description;
    private String image;
    private String status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
