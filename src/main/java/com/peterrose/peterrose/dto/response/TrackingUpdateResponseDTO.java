package com.peterrose.peterrose.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracking Update Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingUpdateResponseDTO {

    private Long id;
    private String status;
    private LocalDateTime timestamp;
    private String description;
}