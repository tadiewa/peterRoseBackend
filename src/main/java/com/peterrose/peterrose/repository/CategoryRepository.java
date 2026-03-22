package com.peterrose.peterrose.repository;

import com.peterrose.peterrose.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    
    // Find by name (unique identifier like "roses", "bouquets")
    Optional<Category> findByName(String name);
    
    // Find all active categories
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();
    
    // Find by display name
    Optional<Category> findByDisplayName(String displayName);
    
    // Check if category name exists
    boolean existsByName(String name);
}
