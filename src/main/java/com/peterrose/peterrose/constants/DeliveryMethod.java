package com.peterrose.peterrose.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeliveryMethod {
    DELIVERY("delivery"),
    COLLECTION("collection");

    private final String value;

    DeliveryMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static DeliveryMethod fromValue(String value) {
        for (DeliveryMethod method : DeliveryMethod.values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown delivery method: " + value);
    }
}
