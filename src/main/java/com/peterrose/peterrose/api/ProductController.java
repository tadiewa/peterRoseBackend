package com.peterrose.peterrose.api;

import com.peterrose.peterrose.dto.request.ProductCreateDTO;
import com.peterrose.peterrose.dto.request.ProductUpdateDTO;
import com.peterrose.peterrose.dto.response.ProductResponseDTO;
import com.peterrose.peterrose.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search
    ) {
        if (featured != null && featured) {
            return ResponseEntity.ok(productService.getFeaturedProducts());
        }
        
        if (categoryId != null) {
            return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
        }
        
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(productService.searchProducts(search));
        }
        
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    // GET /api/products/best-sellers
    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductResponseDTO>> getBestSellers() {
        return ResponseEntity.ok(productService.getBestSellers());
    }
    
    // GET /api/products/low-stock
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer threshold
    ) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }
    
    // GET /api/products/{id} - Get single product
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/products - Create new product
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductResponseDTO created = productService.createProduct(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT /api/products/{id} - Update product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateDTO updateDTO
    ) {
        ProductResponseDTO updated = productService.updateProduct(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDTO> updateStock(
            @PathVariable String id,
            @RequestBody Map<String, Integer> request
    ) {
        Integer stock = request.get("stock");
        if (stock == null) {
            return ResponseEntity.badRequest().build();
        }
        ProductResponseDTO updated = productService.updateStock(id, stock);
        return ResponseEntity.ok(updated);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
