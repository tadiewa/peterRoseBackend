package com.peterrose.peterrose.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateStatusDTO {

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "^(pending|confirmed|preparing|out-for-delivery|delivered|cancelled|ready-for-collection)$",
            message = "Invalid order status"
    )
    private String status;

    private String description;
}