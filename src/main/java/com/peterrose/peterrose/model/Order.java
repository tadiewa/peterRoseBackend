package com.peterrose.peterrose.model;

import com.peterrose.peterrose.constants.DeliveryMethod;
import com.peterrose.peterrose.constants.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing a customer order
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    private String id; // Format: "ORD-2026-001"
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    @JsonProperty("deliveryMethod")
    private DeliveryMethod deliveryMethod;
    
    @Column(name = "delivery_date")
    @JsonProperty("deliveryDate")
    private LocalDate deliveryDate;
    
    @Column(name = "delivery_time")
    @JsonProperty("deliveryTime")
    private String deliveryTime;
    
    @Column(name = "delivery_cost")
    @JsonProperty("deliveryCost")
    private Double deliveryCost;
    
    @Column(nullable = false)
    private Double subtotal;
    
    @Column(nullable = false)
    private Double total;
    
    @Embedded
    private Customer customer;
    
    @Column(name = "created_at", nullable = false)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @JsonProperty("trackingUpdates")
    private List<TrackingUpdate> trackingUpdates = new ArrayList<>();
    
    @Column(name = "collection_point_id")
    @JsonProperty("collectionPointId")
    private String collectionPointId;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (id == null) {
            id = generateOrderId();
        }
    }
    
    // Generate order ID in format ORD-YYYY-NNN
    private String generateOrderId() {
        int year = LocalDateTime.now().getYear();
        long timestamp = System.currentTimeMillis() % 1000;
        return String.format("ORD-%d-%03d", year, timestamp);
    }
}
