package com.peterrose.peterrose.service;

import com.peterrose.peterrose.dto.request.CategoryCreateDTO;
import com.peterrose.peterrose.dto.request.CategoryUpdateDTO;
import com.peterrose.peterrose.dto.response.CategoryResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * Category service interface - uses DTOs
 */
public interface CategoryService {
    
    List<CategoryResponseDTO> getAllCategories();
    
    List<CategoryResponseDTO> getActiveCategories();
    
    Optional<CategoryResponseDTO> getCategoryById(String id);
    
    Optional<CategoryResponseDTO> getCategoryByName(String name);
    
    CategoryResponseDTO createCategory(CategoryCreateDTO createDTO);
    
    CategoryResponseDTO updateCategory(String id, CategoryUpdateDTO updateDTO);
    
    void deleteCategory(String id);
    
    CategoryResponseDTO toggleCategoryStatus(String id);
}
