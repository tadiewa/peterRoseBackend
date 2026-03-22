package com.peterrose.peterrose.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customer embedded entity for order customer information
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Column(name = "customer_first_name", nullable = false)
    private String firstName;
    
    @Column(name = "customer_last_name", nullable = false)
    private String lastName;
    
    @Column(name = "customer_email", nullable = false)
    private String email;
    
    @Column(name = "customer_phone", nullable = false)
    private String phone;
    
    @Column(name = "customer_address")
    private String address;
    
    @Column(name = "customer_city")
    private String city;
    
    @Column(name = "customer_postal_code")
    private String postalCode;
    
    @Column(name = "customer_province")
    private String province;
}
