package com.peterrose.peterrose.mapper;

import com.peterrose.peterrose.dto.request.CategoryCreateDTO;
import com.peterrose.peterrose.dto.request.CategoryUpdateDTO;
import com.peterrose.peterrose.dto.response.CategoryResponseDTO;
import com.peterrose.peterrose.model.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Category entity <-> DTOs
 */
@Component
public class CategoryMapper {
    
    /**
     * Convert Entity to Response DTO
     */
    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .displayName(category.getDisplayName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive())
                .displayOrder(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert list of entities to list of response DTOs
     */
    public List<CategoryResponseDTO> toResponseDTOList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Create DTO to Entity
     */
    public Category toEntity(CategoryCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Category category = new Category();
        category.setName(dto.getName());
        category.setDisplayName(dto.getDisplayName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setActive(dto.getActive() != null ? dto.getActive() : true);
        category.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        
        return category;
    }
    
    /**
     * Update entity from Update DTO
     */
    public void updateEntityFromDTO(Category category, CategoryUpdateDTO dto) {
        if (category == null || dto == null) {
            return;
        }
        
        category.setName(dto.getName());
        category.setDisplayName(dto.getDisplayName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        
        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }
        if (dto.getDisplayOrder() != null) {
            category.setDisplayOrder(dto.getDisplayOrder());
        }
    }
}
