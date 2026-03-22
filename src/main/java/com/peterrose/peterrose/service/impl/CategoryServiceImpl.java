package com.peterrose.peterrose.service.impl;

import com.peterrose.peterrose.dto.request.CategoryCreateDTO;
import com.peterrose.peterrose.dto.request.CategoryUpdateDTO;
import com.peterrose.peterrose.dto.response.CategoryResponseDTO;
import com.peterrose.peterrose.exception.DuplicateResourceException;
import com.peterrose.peterrose.exception.ResourceNotFoundException;
import com.peterrose.peterrose.mapper.CategoryMapper;
import com.peterrose.peterrose.model.Category;
import com.peterrose.peterrose.repository.CategoryRepository;
import com.peterrose.peterrose.repository.ProductRepository;
import com.peterrose.peterrose.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Category service implementation with DTO layer
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseDTOList(categories);
    }
    
    @Override
    public List<CategoryResponseDTO> getActiveCategories() {
        List<Category> categories = categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
        return categoryMapper.toResponseDTOList(categories);
    }
    
    @Override
    public Optional<CategoryResponseDTO> getCategoryById(String id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponseDTO);
    }
    
    @Override
    public Optional<CategoryResponseDTO> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toResponseDTO);
    }
    
    @Override
    public CategoryResponseDTO createCategory(CategoryCreateDTO createDTO) {
        // Check for duplicates
        if (categoryRepository.existsByName(createDTO.getName())) {
            throw new DuplicateResourceException("Category", "name", createDTO.getName());
        }
        
        // Convert DTO to entity
        Category category = categoryMapper.toEntity(createDTO);
        
        // Save entity
        Category savedCategory = categoryRepository.save(category);
        
        // Return DTO
        return categoryMapper.toResponseDTO(savedCategory);
    }
    
    @Override
    public CategoryResponseDTO updateCategory(String id, CategoryUpdateDTO updateDTO) {
        // Find existing category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Check for duplicate name
        if (!category.getName().equals(updateDTO.getName()) && 
            categoryRepository.existsByName(updateDTO.getName())) {
            throw new DuplicateResourceException("Category", "name", updateDTO.getName());
        }
        
        // Update entity from DTO
        categoryMapper.updateEntityFromDTO(category, updateDTO);
        
        // Save updated entity
        Category updatedCategory = categoryRepository.save(category);
        
        // Return DTO
        return categoryMapper.toResponseDTO(updatedCategory);
    }
    
    @Override
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }

        boolean hasProducts = !productRepository.findByCategoryId(id).isEmpty();
        if (hasProducts) {
            throw new IllegalStateException(
                    "Cannot delete category because it still has products assigned to it. " +
                    "Please reassign or delete the products first."
            );
        }

        categoryRepository.deleteById(id);
    }
    
    @Override
    public CategoryResponseDTO toggleCategoryStatus(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        category.setActive(!category.getActive());
        Category updatedCategory = categoryRepository.save(category);
        
        return categoryMapper.toResponseDTO(updatedCategory);
    }
}
