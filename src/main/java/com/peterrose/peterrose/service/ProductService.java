package com.peterrose.peterrose.service;

import com.peterrose.peterrose.dto.request.ProductCreateDTO;
import com.peterrose.peterrose.dto.request.ProductUpdateDTO;
import com.peterrose.peterrose.dto.response.ProductResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * Product service interface - uses DTOs for API layer
 * Entities stay internal to the service layer
 */
public interface ProductService {
    
    List<ProductResponseDTO> getAllProducts();
    
    Optional<ProductResponseDTO> getProductById(String id);
    
    List<ProductResponseDTO> getFeaturedProducts();
    
    List<ProductResponseDTO> getBestSellers();
    
    List<ProductResponseDTO> getProductsByCategoryId(String categoryId);
    
    List<ProductResponseDTO> getLowStockProducts(Integer threshold);
    
    List<ProductResponseDTO> searchProducts(String query);
    
    ProductResponseDTO createProduct(ProductCreateDTO createDTO);
    
    ProductResponseDTO updateProduct(String id, ProductUpdateDTO updateDTO);
    
    ProductResponseDTO updateStock(String id, Integer stock);
    
    void deleteProduct(String id);
}
