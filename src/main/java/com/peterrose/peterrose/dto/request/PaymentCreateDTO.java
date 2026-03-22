package com.peterrose.peterrose.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for initiating a payment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;
    
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "ZAR", message = "Only ZAR currency is supported")
    private String currency = "ZAR";
    
    @NotBlank(message = "Success URL is required")
    private String successUrl;
    
    @NotBlank(message = "Cancel URL is required")
    private String cancelUrl;
    
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    private String customerName;
}
