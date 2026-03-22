package com.peterrose.peterrose.mapper;

import com.peterrose.peterrose.dto.request.ProductCreateDTO;
import com.peterrose.peterrose.dto.request.ProductUpdateDTO;
import com.peterrose.peterrose.dto.response.ProductResponseDTO;
import com.peterrose.peterrose.model.Product;
import com.peterrose.peterrose.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Product entity <-> DTOs
 * Separates internal model from API contracts
 */
@Component
@RequiredArgsConstructor
public class ProductMapper {
    
    private final CategoryMapper categoryMapper;
    
    /**
     * Convert Entity to Response DTO
     */
    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .image(product.getImage())
                .category(categoryMapper.toResponseDTO(product.getCategory()))
                .stock(product.getStock())
                .featured(product.getFeatured())
                .bestSeller(product.getBestSeller())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert list of entities to list of response DTOs
     */
    public List<ProductResponseDTO> toResponseDTOList(List<Product> products) {
        return products.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Create DTO to Entity
     */
    public Product toEntity(ProductCreateDTO dto, Category category) {
        if (dto == null) {
            return null;
        }
        
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setCategory(category);
        product.setStock(dto.getStock());
        product.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
        product.setBestSeller(dto.getBestSeller() != null ? dto.getBestSeller() : false);
        product.setRating(0.0);
        product.setReviewCount(0);
        
        return product;
    }
    
    /**
     * Update entity from Update DTO
     */
    public void updateEntityFromDTO(Product product, ProductUpdateDTO dto, Category category) {
        if (product == null || dto == null) {
            return;
        }
        
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setCategory(category);
        product.setStock(dto.getStock());
        
        if (dto.getFeatured() != null) {
            product.setFeatured(dto.getFeatured());
        }
        if (dto.getBestSeller() != null) {
            product.setBestSeller(dto.getBestSeller());
        }
        if (dto.getRating() != null) {
            product.setRating(dto.getRating());
        }
        if (dto.getReviewCount() != null) {
            product.setReviewCount(dto.getReviewCount());
        }
    }
}
