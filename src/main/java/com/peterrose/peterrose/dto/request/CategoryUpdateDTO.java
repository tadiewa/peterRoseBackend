package com.peterrose.peterrose.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating categories
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {
    
    @NotBlank(message = "Category name is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Name must be lowercase, alphanumeric, and hyphens only")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private String imageUrl;
    
    private Boolean active;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder;
}
