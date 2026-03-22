package com.peterrose.peterrose.constants;

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