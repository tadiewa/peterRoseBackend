package com.peterrose.peterrose.api;

import com.peterrose.peterrose.dto.request.CategoryCreateDTO;
import com.peterrose.peterrose.dto.request.CategoryUpdateDTO;
import com.peterrose.peterrose.dto.response.CategoryResponseDTO;
import com.peterrose.peterrose.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    // GET /api/categories - Get all categories (or active only)
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(
            @RequestParam(required = false) Boolean active
    ) {
        if (active != null && active) {
            return ResponseEntity.ok(categoryService.getActiveCategories());
        }
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
    // GET /api/categories/{id} - Get single category
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/categories/by-name/{name} - Get category by name
    @GetMapping("/by-name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/categories - Create new category
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryCreateDTO createDTO) {
        CategoryResponseDTO created = categoryService.createCategory(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT /api/categories/{id} - Update category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryUpdateDTO updateDTO
    ) {
        CategoryResponseDTO updated = categoryService.updateCategory(id, updateDTO);
        return ResponseEntity.ok(updated);
    }
    
    // PATCH /api/categories/{id}/toggle - Activate/Deactivate
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<CategoryResponseDTO> toggleCategoryStatus(@PathVariable String id) {
        CategoryResponseDTO updated = categoryService.toggleCategoryStatus(id);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE /api/categories/{id} - Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
