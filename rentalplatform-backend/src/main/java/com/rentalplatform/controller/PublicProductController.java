package com.rentalplatform.controller;

import com.rentalplatform.dto.ProductDTO;
import com.rentalplatform.model.Category;
import com.rentalplatform.model.Product;
import com.rentalplatform.repository.CategoryRepository;
import com.rentalplatform.repository.ProductRepository;
import com.rentalplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Get all active and available products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productRepository.findByIsActiveTrueAndIsAvailableTrue();
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    // Get products by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        List<Product> products = productRepository.findByCategoryAndIsActiveTrueAndIsAvailableTrue(category);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    // Get product by ID (public view)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        if (!product.getIsActive() || !product.getIsAvailable()) {
            throw new RuntimeException("Produit non disponible");
        }

        return ResponseEntity.ok(convertToDTO(product));
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPricePerDay(product.getPricePerDay());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setOwnerId(product.getOwner().getId());
        dto.setOwnerName(product.getOwner().getFullName());
        dto.setLocation(product.getLocation());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsAvailable(product.getIsAvailable());
        dto.setIsActive(product.getIsActive());
        dto.setSpecifications(product.getSpecifications());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}