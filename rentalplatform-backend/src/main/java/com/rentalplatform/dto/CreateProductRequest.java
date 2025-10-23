package com.rentalplatform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Le nom du produit est requis")
    private String name;

    private String description;

    @NotNull(message = "Le prix par jour est requis")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    private BigDecimal pricePerDay;

    @NotNull(message = "La catégorie est requise")
    private Long categoryId;

    private String location;

    private String imageUrl;

    private Boolean isAvailable = true;

    private String specifications;
}