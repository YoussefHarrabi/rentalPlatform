package com.rentalplatform.service;

import com.rentalplatform.dto.CreateProductRequest;
import com.rentalplatform.dto.ProductDTO;
import com.rentalplatform.dto.UpdateProductRequest;
import com.rentalplatform.model.Category;
import com.rentalplatform.model.Product;
import com.rentalplatform.model.User;
import com.rentalplatform.repository.CategoryRepository;
import com.rentalplatform.repository.ProductRepository;
import com.rentalplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;


    // Get all products for a specific owner
    public List<ProductDTO> getOwnerProducts(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));

        return productRepository.findByOwner(owner).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get active products for a specific owner
    public List<ProductDTO> getOwnerActiveProducts(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));

        return productRepository.findByOwnerAndIsActiveTrue(owner).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get product by ID
    public ProductDTO getProductById(Long id, String ownerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier que l'utilisateur est bien le propriétaire
        if (!product.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à accéder à ce produit");
        }

        return convertToDTO(product);
    }

    // Create product
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        if (!category.getIsActive()) {
            throw new RuntimeException("Cette catégorie n'est pas active");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPricePerDay(request.getPricePerDay());
        product.setCategory(category);
        product.setOwner(owner);
        product.setLocation(request.getLocation());
        product.setImageUrl(request.getImageUrl());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        product.setIsActive(true);
        product.setSpecifications(request.getSpecifications());

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    // Update product
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request, String ownerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier que l'utilisateur est bien le propriétaire
        if (!product.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPricePerDay(request.getPricePerDay());
        product.setCategory(category);
        product.setLocation(request.getLocation());
        product.setImageUrl(request.getImageUrl());

        if (request.getIsAvailable() != null) {
            product.setIsAvailable(request.getIsAvailable());
        }

        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        product.setSpecifications(request.getSpecifications());

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    // Delete product (soft delete)
    @Transactional
    public void deleteProduct(Long id, String ownerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        if (!product.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce produit");
        }

        // Delete product image if exists
        if (product.getImageUrl() != null && product.getImageUrl().contains("/uploads/")) {
            String filename = product.getImageUrl().substring(product.getImageUrl().lastIndexOf("/") + 1);
            try {
                fileStorageService.deleteFile(filename);
            } catch (Exception e) {
                // Log error but don't fail the deletion
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }

        product.setIsActive(false);
        productRepository.save(product);
    }

    // Toggle availability
    @Transactional
    public ProductDTO toggleAvailability(Long id, String ownerEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier que l'utilisateur est bien le propriétaire
        if (!product.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        product.setIsAvailable(!product.getIsAvailable());
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    // Convert Product to ProductDTO
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