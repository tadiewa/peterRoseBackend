package com.peterrose.peterrose.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "collection_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, length = 500)
    private String address;
    
    @Column(nullable = false)
    private String province;
    
    @Column(nullable = false)
    private String hours;
    
    @Column(nullable = false)
    private Boolean active = true;
}
