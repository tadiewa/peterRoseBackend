package com.peterrose.peterrose.service;

import com.peterrose.peterrose.dto.request.OrderCreateDTO;
import com.peterrose.peterrose.dto.request.OrderUpdateStatusDTO;
import com.peterrose.peterrose.dto.response.OrderResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * Order service interface
 */
public interface OrderService {
    
    List<OrderResponseDTO> getAllOrders();
    
    Optional<OrderResponseDTO> getOrderById(String id);
    
    List<OrderResponseDTO> getOrdersByStatus(String status);
    
    List<OrderResponseDTO> searchOrders(String query);
    
    OrderResponseDTO createOrder(OrderCreateDTO createDTO);
    
    OrderResponseDTO updateOrderStatus(String id, OrderUpdateStatusDTO updateDTO);
    
    void deleteOrder(String id);
}
