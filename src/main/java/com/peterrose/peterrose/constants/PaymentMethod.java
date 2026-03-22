package com.peterrose.peterrose.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment method enum
 */
public enum PaymentMethod {
    CARD("card"),
    EFT("eft"),
    SNAPSCAN("snapscan"),
    CASH("cash");
    
    private final String value;
    
    PaymentMethod(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + value);
    }
}
