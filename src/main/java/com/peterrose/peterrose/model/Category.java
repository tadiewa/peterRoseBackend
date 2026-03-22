package com.peterrose.peterrose.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name; // "roses", "bouquets", "boxed", "treats", "valentines"

    @Column(name = "display_name", nullable = false)
    @JsonProperty("displayName")
    private String displayName; // "Roses", "Bouquets", "Boxed Arrangements"

    @Column(length = 1000)
    private String description;

    @Column(name = "image_url")
    @JsonProperty("imageUrl")
    private String imageUrl; // Category thumbnail image

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "display_order")
    @JsonProperty("displayOrder")
    private Integer displayOrder = 0; // For sorting categories

    @Column(name = "created_at")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}