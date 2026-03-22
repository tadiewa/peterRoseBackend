package com.peterrose.peterrose.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private String id;
    private List<OrderItemResponseDTO> items;
    private String status;
    private String deliveryMethod;
    private LocalDate deliveryDate;
    private String deliveryTime;
    private Double deliveryCost;
    private Double subtotal;
    private Double total;
    private CustomerResponseDTO customer;
    private LocalDateTime createdAt;
    private List<TrackingUpdateResponseDTO> trackingUpdates;
    private String collectionPointId;
}