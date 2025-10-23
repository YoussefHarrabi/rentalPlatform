package com.rentalplatform.repository;

import com.rentalplatform.model.Product;
import com.rentalplatform.model.User;
import com.rentalplatform.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOwner(User owner);
    List<Product> findByCategory(Category category);
    List<Product> findByIsActiveTrue();
    List<Product> findByIsActiveTrueAndIsAvailableTrue();
    List<Product> findByOwnerAndIsActiveTrue(User owner);
    List<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrue(Category category);
}