package com.peterrose.peterrose.mapper;

import com.peterrose.peterrose.dto.request.CustomerCreateDTO;
import com.peterrose.peterrose.dto.request.OrderItemCreateDTO;
import com.peterrose.peterrose.dto.response.*;
import com.peterrose.peterrose.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Order entity <-> DTOs
 */
@Component
@RequiredArgsConstructor
public class OrderMapper {
    
    private final ProductMapper productMapper;
    
    /**
     * Convert Order Entity to Response DTO
     */
    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }
        
        return OrderResponseDTO.builder()
                .id(order.getId())
                .items(toOrderItemResponseDTOList(order.getItems()))
                .status(order.getStatus().getValue())
                .deliveryMethod(order.getDeliveryMethod().getValue())
                .deliveryDate(order.getDeliveryDate())
                .deliveryTime(order.getDeliveryTime())
                .deliveryCost(order.getDeliveryCost())
                .subtotal(order.getSubtotal())
                .total(order.getTotal())
                .customer(toCustomerResponseDTO(order.getCustomer()))
                .createdAt(order.getCreatedAt())
                .trackingUpdates(toTrackingUpdateResponseDTOList(order.getTrackingUpdates()))
                .collectionPointId(order.getCollectionPointId())
                .build();
    }
    
    /**
     * Convert list of Orders to Response DTOs
     */
    public List<OrderResponseDTO> toResponseDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert OrderItem Entity to Response DTO
     */
    public OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .product(productMapper.toResponseDTO(item.getProduct()))
                .quantity(item.getQuantity())
                .priceAtOrder(item.getPriceAtOrder())
                .build();
    }
    
    /**
     * Convert list of OrderItems to Response DTOs
     */
    public List<OrderItemResponseDTO> toOrderItemResponseDTOList(List<OrderItem> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(this::toOrderItemResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Customer Embeddable to Response DTO
     */
    public CustomerResponseDTO toCustomerResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        return CustomerResponseDTO.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .postalCode(customer.getPostalCode())
                .province(customer.getProvince())
                .build();
    }
    
    /**
     * Convert TrackingUpdate Entity to Response DTO
     */
    public TrackingUpdateResponseDTO toTrackingUpdateResponseDTO(TrackingUpdate update) {
        if (update == null) {
            return null;
        }
        
        return TrackingUpdateResponseDTO.builder()
                .id(update.getId())
                .status(update.getStatus())
                .timestamp(update.getTimestamp())
                .description(update.getDescription())
                .build();
    }
    
    /**
     * Convert list of TrackingUpdates to Response DTOs
     */
    public List<TrackingUpdateResponseDTO> toTrackingUpdateResponseDTOList(List<TrackingUpdate> updates) {
        if (updates == null) {
            return null;
        }
        return updates.stream()
                .map(this::toTrackingUpdateResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Create DTO to Customer Embeddable
     */
    public Customer toCustomerEntity(CustomerCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setCity(dto.getCity());
        customer.setPostalCode(dto.getPostalCode());
        customer.setProvince(dto.getProvince());
        
        return customer;
    }
    
    /**
     * Convert Create DTO to OrderItem Entity
     */
    public OrderItem toOrderItemEntity(OrderItemCreateDTO dto, Product product) {
        if (dto == null) {
            return null;
        }
        
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPriceAtOrder(product.getPrice()); // Capture price at order time
        
        return item;
    }
}
