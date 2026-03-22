package com.peterrose.peterrose.service.impl;

import com.peterrose.peterrose.dto.request.ProductCreateDTO;
import com.peterrose.peterrose.dto.request.ProductUpdateDTO;
import com.peterrose.peterrose.dto.response.ProductResponseDTO;
import com.peterrose.peterrose.exception.ResourceNotFoundException;
import com.peterrose.peterrose.mapper.ProductMapper;
import com.peterrose.peterrose.model.Category;
import com.peterrose.peterrose.model.Product;
import com.peterrose.peterrose.repository.CategoryRepository;
import com.peterrose.peterrose.repository.ProductRepository;
import com.peterrose.peterrose.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    
    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toResponseDTOList(products);
    }
    
    @Override
    public Optional<ProductResponseDTO> getProductById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toResponseDTO);
    }
    
    @Override
    public List<ProductResponseDTO> getFeaturedProducts() {
        List<Product> products = productRepository.findByFeaturedTrue();
        return productMapper.toResponseDTOList(products);
    }
    
    @Override
    public List<ProductResponseDTO> getBestSellers() {
        List<Product> products = productRepository.findByBestSellerTrue();
        return productMapper.toResponseDTOList(products);
    }
    
    @Override
    public List<ProductResponseDTO> getProductsByCategoryId(String categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return productMapper.toResponseDTOList(products);
    }
    
    @Override
    public List<ProductResponseDTO> getLowStockProducts(Integer threshold) {
        List<Product> products = productRepository.findByStockLessThanEqual(threshold);
        return productMapper.toResponseDTOList(products);
    }
    
    @Override
    public List<ProductResponseDTO> searchProducts(String query) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(query);
        return productMapper.toResponseDTOList(products);
    }
    
    @Override
    public ProductResponseDTO createProduct(ProductCreateDTO createDTO) {
        Category category = categoryRepository.findById(createDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", createDTO.getCategoryId()));
        Product product = productMapper.toEntity(createDTO, category);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }
    
    @Override
    public ProductResponseDTO updateProduct(String id, ProductUpdateDTO updateDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        Category category = categoryRepository.findById(updateDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", updateDTO.getCategoryId()));
        productMapper.updateEntityFromDTO(product, updateDTO, category);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(updatedProduct);
    }
    
    @Override
    public ProductResponseDTO updateStock(String id, Integer stock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.setStock(stock);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(updatedProduct);
    }
    
    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }
}
