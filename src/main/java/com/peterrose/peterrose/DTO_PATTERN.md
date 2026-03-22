# DTO Pattern - Why We Don't Expose Entities

## 🚫 The Problem: Exposing Entities Directly

**Before (BAD):**
```java
@RestController
public class ProductController {
    
    @GetMapping("/api/products")
    public List<Product> getProducts() {
        return productService.getAllProducts(); // ❌ Exposing entity
    }
}
```

### Why This Is Dangerous:

1. **❌ Security Risk** - Exposes internal database structure
2. **❌ Tight Coupling** - Frontend tied to database schema
3. **❌ No Flexibility** - Can't change DB without breaking API
4. **❌ Performance Issues** - Lazy loading, circular references, N+1 queries
5. **❌ Validation Confusion** - Different rules for create vs update
6. **❌ Over-fetching** - Returns fields the client doesn't need

---

## ✅ The Solution: DTO Pattern

**After (GOOD):**
```java
@RestController
public class ProductController {
    
    @GetMapping("/api/products")
    public List<ProductResponseDTO> getProducts() {
        return productService.getAllProducts(); // ✅ Returns DTO
    }
}
```

---

## 📁 DTO Structure

```
dto/
├── request/                    ← Input DTOs (what client sends)
│   ├── ProductCreateDTO.java  ← For POST requests
│   ├── ProductUpdateDTO.java  ← For PUT requests
│   ├── CategoryCreateDTO.java
│   └── CategoryUpdateDTO.java
│
└── response/                   ← Output DTOs (what API returns)
    ├── ProductResponseDTO.java
    └── CategoryResponseDTO.java
```

---

## 🔄 Data Flow

```
Client Request (JSON)
    ↓
ProductCreateDTO (@Valid validates)
    ↓
Controller → Service
    ↓
Mapper converts DTO → Entity
    ↓
Repository saves Entity
    ↓
Mapper converts Entity → DTO
    ↓
Controller returns ProductResponseDTO
    ↓
Client Response (JSON)
```

**Entities NEVER leave the service layer!**

---

## 📝 Example: Creating a Product

### 1. Client sends ProductCreateDTO

```json
POST /api/products
{
  "name": "Red Roses",
  "description": "Beautiful red roses",
  "price": 500,
  "image": "/images/roses.jpg",
  "categoryId": "cat-123",
  "stock": 10,
  "featured": true
}
```

### 2. DTO with Validation

```java
@Data
public class ProductCreateDTO {
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    private String name;
    
    @Positive(message = "Price must be positive")
    private Double price;
    
    @NotBlank(message = "Category ID is required")
    private String categoryId; // ✅ Just ID, not full entity
    
    // ... other fields
}
```

### 3. Controller with Validation

```java
@PostMapping
public ResponseEntity<ProductResponseDTO> createProduct(
        @Valid @RequestBody ProductCreateDTO createDTO) {
    
    // @Valid triggers validation
    ProductResponseDTO created = productService.createProduct(createDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

### 4. Service Layer - DTO to Entity

```java
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper mapper;
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    
    public ProductResponseDTO createProduct(ProductCreateDTO createDTO) {
        // Validate category exists
        Category category = categoryRepository.findById(createDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(...));
        
        // Convert DTO → Entity
        Product product = mapper.toEntity(createDTO, category);
        
        // Save entity
        Product saved = repository.save(product);
        
        // Convert Entity → DTO
        return mapper.toResponseDTO(saved);
    }
}
```

### 5. Mapper Does Conversion

```java
@Component
public class ProductMapper {
    
    public Product toEntity(ProductCreateDTO dto, Category category) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCategory(category); // ✅ Full entity internally
        // ... other fields
        return product;
    }
    
    public ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .category(categoryMapper.toResponseDTO(product.getCategory()))
                // ... other fields
                .build();
    }
}
```

### 6. API Returns ProductResponseDTO

```json
201 Created
{
  "id": "prod-456",
  "name": "Red Roses",
  "description": "Beautiful red roses",
  "price": 500,
  "image": "/images/roses.jpg",
  "category": {
    "id": "cat-123",
    "name": "roses",
    "displayName": "Roses"
  },
  "stock": 10,
  "featured": true,
  "bestSeller": false,
  "rating": 0,
  "reviewCount": 0,
  "createdAt": "2026-03-13T10:30:00",
  "updatedAt": "2026-03-13T10:30:00"
}
```

---

## 🎯 Benefits of DTOs

### 1. **Security**
```java
// Entity (internal)
@Entity
public class Product {
    private String id;
    private String secretSupplierCode; // ❌ Don't expose this
    private Double costPrice;          // ❌ Don't expose this
    private Double sellingPrice;       // ✅ Expose as "price"
}

// DTO (external)
public class ProductResponseDTO {
    private String id;
    private Double price;  // ✅ Only expose what's needed
    // secretSupplierCode not included
}
```

### 2. **Decoupling**
```java
// Can change entity without breaking API
@Entity
public class Product {
    private String name;
    // Renamed in DB: title → name
}

// API contract stays the same
public class ProductResponseDTO {
    private String name; // ✅ API unchanged
}
```

### 3. **Different Validation for Create vs Update**
```java
// Create: category is required
public class ProductCreateDTO {
    @NotBlank
    private String categoryId;
}

// Update: category is optional (don't force re-selection)
public class ProductUpdateDTO {
    private String categoryId; // Optional
}
```

### 4. **Performance - Avoid N+1 Queries**
```java
// Entity with lazy loading
@Entity
public class Product {
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category; // ❌ Causes N+1 if exposed directly
}

// DTO controls exactly what's loaded
public class ProductResponseDTO {
    private CategoryResponseDTO category; // ✅ Explicitly loaded
}
```

### 5. **Backward Compatibility**
```java
// Add new field to entity
@Entity
public class Product {
    private String supplierEmail; // New field
}

// Don't expose in old API version
public class ProductResponseDTO {
    // supplierEmail not included
    // Old clients unaffected
}
```

---

## 📋 DTO Naming Conventions

| Purpose | Naming | Example |
|---------|--------|---------|
| Create | `{Entity}CreateDTO` | `ProductCreateDTO` |
| Update | `{Entity}UpdateDTO` | `ProductUpdateDTO` |
| Response | `{Entity}ResponseDTO` | `ProductResponseDTO` |
| List Response | `{Entity}ListResponseDTO` | `ProductListResponseDTO` |
| Search Request | `{Entity}SearchRequestDTO` | `ProductSearchRequestDTO` |

---

## 🧪 Testing with DTOs

```java
@Test
void shouldCreateProduct() {
    // Arrange
    ProductCreateDTO createDTO = ProductCreateDTO.builder()
            .name("Red Roses")
            .price(500.0)
            .categoryId("cat-123")
            .build();
    
    // Act
    ProductResponseDTO response = productService.createProduct(createDTO);
    
    // Assert
    assertNotNull(response.getId());
    assertEquals("Red Roses", response.getName());
}
```

---

## ⚠️ Common Mistakes to Avoid

### ❌ Mistake 1: Reusing DTOs
```java
// ❌ BAD - Using same DTO for create and update
public class ProductDTO {
    private String id;        // Not needed for create
    private LocalDateTime createdAt; // Not needed for update
}
```

### ✅ Solution: Separate DTOs
```java
// ✅ GOOD - Separate DTOs
public class ProductCreateDTO {
    // No id, no createdAt
}

public class ProductUpdateDTO {
    // No createdAt
}
```

### ❌ Mistake 2: Exposing Entity in DTO
```java
// ❌ BAD
public class ProductResponseDTO {
    private Category category; // Exposing entity
}
```

### ✅ Solution: Nested DTO
```java
// ✅ GOOD
public class ProductResponseDTO {
    private CategoryResponseDTO category; // DTO all the way down
}
```

### ❌ Mistake 3: Manual Mapping in Controllers
```java
// ❌ BAD - Mapping in controller
@PostMapping
public ProductResponseDTO create(@RequestBody ProductCreateDTO dto) {
    Product product = new Product();
    product.setName(dto.getName()); // Manual mapping
    // ... 20 more lines
}
```

### ✅ Solution: Use Mapper
```java
// ✅ GOOD - Delegate to mapper
@PostMapping
public ProductResponseDTO create(@Valid @RequestBody ProductCreateDTO dto) {
    return productService.createProduct(dto); // Clean
}
```

---

## 📚 Summary

| Layer | Data Type | Purpose |
|-------|-----------|---------|
| **Controller** | DTOs only | API contract |
| **Service** | DTOs → Entities → DTOs | Business logic |
| **Repository** | Entities only | Database access |
| **Database** | Entities only | Persistence |

**Golden Rule:** Entities are internal implementation details. DTOs are the public API contract.

---

## 🔗 Related Patterns

- **Mapper Pattern** - Converts between entities and DTOs
- **Builder Pattern** - Creates DTOs fluently
- **Validation** - `@Valid` with `@NotNull`, `@Size`, etc.
- **Exception Handling** - GlobalExceptionHandler for validation errors
