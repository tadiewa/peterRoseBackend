package com.peterrose.peterrose.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment status enum
 */
public enum PaymentStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    SUCCEEDED("succeeded"),
    FAILED("failed"),
    CANCELLED("cancelled"),
    REFUNDED("refunded");
    
    private final String value;
    
    PaymentStatus(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid payment status: " + value);
    }
}
