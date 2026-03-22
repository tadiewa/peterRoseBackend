# JWT AUTHENTICATION - COMPLETE BACKEND SETUP

## 📁 FILE STRUCTURE

```
src/main/java/com/peterrose/peterrose/
├── model/
│   ├── User.java                          ✅ Already created
│   └── UserRole.java                      ✅ Already created
├── repository/
│   └── UserRepository.java                ← CREATE THIS
├── dto/
│   ├── request/
│   │   └── LoginRequestDTO.java           ✅ Already created
│   └── response/
│       └── LoginResponseDTO.java          ✅ Already created
├── service/
│   ├── AuthService.java                   ✅ Already created
│   └── impl/
│       ├── AuthServiceImpl.java           ✅ Already created
│       └── JwtService.java                ✅ Already created
├── controller/
│   └── AuthController.java                ✅ Already created
├── security/
│   ├── JwtAuthenticationFilter.java       ✅ Just created
│   └── CustomUserDetailsService.java      ✅ Just created
└── config/
    ├── SecurityConfig.java                ✅ Just created
    └── AdminDataLoader.java               ✅ Just created
```

---

## 📝 FILES TO CREATE

### 1. UserRepository.java
Location: `src/main/java/com/peterrose/peterrose/repository/UserRepository.java`

```java
package com.peterrose.peterrose.repository;

import com.peterrose.peterrose.model.User;
import com.peterrose.peterrose.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    boolean existsByEmail(String email);
}
```

### 2. UserRole.java (if not exists)
Location: `src/main/java/com/peterrose/peterrose/model/UserRole.java`

```java
package com.peterrose.peterrose.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN("admin"),
    CUSTOMER("customer");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
```

### 3. User.java (Already exists - just verify)
Location: `src/main/java/com/peterrose/peterrose/model/User.java`

Already created ✅

### 4. LoginRequestDTO.java (Already created)
Location: `src/main/java/com/peterrose/peterrose/dto/request/LoginRequestDTO.java`

Already created ✅

### 5. LoginResponseDTO.java (Already created)
Location: `src/main/java/com/peterrose/peterrose/dto/response/LoginResponseDTO.java`

Already created ✅

### 6. JwtService.java (Already created)
Location: `src/main/java/com/peterrose/peterrose/service/impl/JwtService.java`

Already created ✅

### 7. AuthService.java (Already created)
Location: `src/main/java/com/peterrose/peterrose/service/AuthService.java`

Already created ✅

### 8. AuthServiceImpl.java (Already created)
Location: `src/main/java/com/peterrose/peterrose/service/impl/AuthServiceImpl.java`

Already created ✅

### 9. AuthController.java (Already created)
Location: `src/main/java/com/peterrose/peterrose/controller/AuthController.java`

Already created ✅

### 10. JwtAuthenticationFilter.java (Just created)
Location: `src/main/java/com/peterrose/peterrose/security/JwtAuthenticationFilter.java`

See SecurityConfig.java file ✅

### 11. CustomUserDetailsService.java (Just created)
Location: `src/main/java/com/peterrose/peterrose/security/CustomUserDetailsService.java`

See CustomUserDetailsService.java file ✅

### 12. SecurityConfig.java (Just created)
Location: `src/main/java/com/peterrose/peterrose/config/SecurityConfig.java`

See SecurityConfig.java file ✅

### 13. AdminDataLoader.java (Just created)
Location: `src/main/java/com/peterrose/peterrose/config/AdminDataLoader.java`

See AdminDataLoader.java file ✅

---

## ⚙️ APPLICATION.YML

Update your `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: peterrose
  
  datasource:
    url: jdbc:postgresql://localhost:5432/peterose
    username: postgres
    password: tadiewa
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  main:
    allow-bean-definition-overriding: true
  
  servlet:
    multipart:
      enabled: true
      max-file-size: 5MB
      max-request-size: 5MB

server:
  port: 9039

# JWT Configuration
jwt:
  secret: ${JWT_SECRET:your-super-secret-jwt-key-change-this-in-production-minimum-256-bits-long}
  expiration: 86400000  # 24 hours

# Yoco Payment
yoco:
  secret:
    key: ${YOCO_SECRET_KEY}

# File Upload
app:
  upload:
    dir: ${user.home}/peterrose-uploads/products
```

---

## 📦 POM.XML DEPENDENCIES

Add to your `pom.xml`:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

---

## 🚀 STARTUP CHECKLIST

1. ✅ Add all files to correct packages
2. ✅ Update pom.xml with dependencies
3. ✅ Update application.yml
4. ✅ Run `mvn clean install`
5. ✅ Start Spring Boot
6. ✅ Check logs for: "Initial admin user created"

---

## 🔐 DEFAULT ADMIN CREDENTIALS

After startup, you can login with:
- **Email:** admin@peterrose.com
- **Password:** Admin@123

⚠️ **CHANGE THIS PASSWORD IMMEDIATELY IN PRODUCTION!**

---

## 🧪 TEST THE AUTH API

```bash
# Login
curl -X POST http://localhost:9039/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@peterrose.com","password":"Admin@123"}'

# You'll get back:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "email": "admin@peterrose.com",
  "firstName": "Admin",
  "lastName": "User",
  "role": "admin"
}
```

---

## 📌 WHAT'S PROTECTED NOW?

### Public (No Auth Required):
- GET /api/products/**
- GET /api/categories/**
- GET /api/images/**
- POST /api/orders/**
- POST /api/payments/**
- POST /api/auth/login

### Admin Only (Requires JWT + ADMIN role):
- POST /api/products/**
- PUT /api/products/**
- DELETE /api/products/**
- POST /api/categories/**
- PUT /api/categories/**
- DELETE /api/categories/**
- POST /api/images/**
- DELETE /api/images/**

Customers can browse as guests! Only admins need to login! ✅
