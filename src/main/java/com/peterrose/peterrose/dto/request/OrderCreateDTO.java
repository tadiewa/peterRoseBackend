package com.peterrose.peterrose.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Order Create DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemCreateDTO> items;

    @NotBlank(message = "Delivery method is required")
    @Pattern(regexp = "^(delivery|collection)$", message = "Delivery method must be 'delivery' or 'collection'")
    private String deliveryMethod;

    @NotNull(message = "Delivery date is required")
    @FutureOrPresent(message = "Delivery date must be today or in the future")
    private LocalDate deliveryDate;

    @NotBlank(message = "Delivery time is required")
    private String deliveryTime;

    @PositiveOrZero(message = "Delivery cost cannot be negative")
    private Double deliveryCost;

    @NotNull(message = "Customer information is required")
    @Valid
    private CustomerCreateDTO customer;

    // Optional: for collection orders
    private String collectionPointId;
}