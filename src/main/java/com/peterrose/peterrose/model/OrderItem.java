package com.peterrose.peterrose.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OrderItem entity representing a product in an order
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    // Price at time of order (in case product price changes later)
    @Column(name = "price_at_order", nullable = false)
    @JsonProperty("priceAtOrder")
    private Double priceAtOrder;
    
    @PrePersist
    protected void onCreate() {
        if (product != null && priceAtOrder == null) {
            priceAtOrder = product.getPrice();
        }
    }
    
    // Calculate line total
    public Double getLineTotal() {
        return priceAtOrder * quantity;
    }
}
