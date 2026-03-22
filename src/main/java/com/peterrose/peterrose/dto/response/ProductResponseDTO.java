package com.peterrose.peterrose.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Product DTO for API responses
 * Decouples API from database entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    
    private String id;
    private String name;
    private String description;
    private Double price;
    private String image;
    
    // Category as nested DTO, not full entity
    private CategoryResponseDTO category;
    
    private Integer stock;
    private Boolean featured;
    private Boolean bestSeller;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
