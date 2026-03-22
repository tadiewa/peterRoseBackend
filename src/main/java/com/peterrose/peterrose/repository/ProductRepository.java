package com.peterrose.peterrose.repository;

import com.peterrose.peterrose.model.Product;
import com.peterrose.peterrose.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    // Find products by category
    List<Product> findByCategory(Category category);
    
    // Find products by category ID
    List<Product> findByCategoryId(String categoryId);
    
    // Find featured products
    List<Product> findByFeaturedTrue();
    
    // Find best sellers
    List<Product> findByBestSellerTrue();
    
    // Find products with low stock
    List<Product> findByStockLessThanEqual(Integer threshold);
    
    // Search by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
}
