package com.rentalplatform.controller;

import com.rentalplatform.dto.CreateProductRequest;
import com.rentalplatform.dto.MessageResponse;
import com.rentalplatform.dto.ProductDTO;
import com.rentalplatform.dto.UpdateProductRequest;
import com.rentalplatform.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class ProductController {

    private final ProductService productService;

    // Get all products for the logged-in owner
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getMyProducts(Authentication authentication) {
        String ownerEmail = authentication.getName();
        List<ProductDTO> products = productService.getOwnerProducts(ownerEmail);
        return ResponseEntity.ok(products);
    }

    // Get active products for the logged-in owner
    @GetMapping("/active")
    public ResponseEntity<List<ProductDTO>> getMyActiveProducts(Authentication authentication) {
        String ownerEmail = authentication.getName();
        List<ProductDTO> products = productService.getOwnerActiveProducts(ownerEmail);
        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @PathVariable Long id,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        ProductDTO product = productService.getProductById(id, ownerEmail);
        return ResponseEntity.ok(product);
    }

    // Create new product
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        ProductDTO product = productService.createProduct(request, ownerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // Update product
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        ProductDTO product = productService.updateProduct(id, request, ownerEmail);
        return ResponseEntity.ok(product);
    }

    // Delete product (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        productService.deleteProduct(id, ownerEmail);
        return ResponseEntity.ok(new MessageResponse("Produit supprimé avec succès"));
    }

    // Toggle product availability
    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<ProductDTO> toggleAvailability(
            @PathVariable Long id,
            Authentication authentication) {
        String ownerEmail = authentication.getName();
        ProductDTO product = productService.toggleAvailability(id, ownerEmail);
        return ResponseEntity.ok(product);
    }
}