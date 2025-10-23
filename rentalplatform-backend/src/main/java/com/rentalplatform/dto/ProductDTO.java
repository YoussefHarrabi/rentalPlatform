package com.rentalplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal pricePerDay;
    private Long categoryId;
    private String categoryName;
    private Long ownerId;
    private String ownerName;
    private String location;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isActive;
    private String specifications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}