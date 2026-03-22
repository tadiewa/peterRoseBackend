package com.peterrose.peterrose.api;

import com.peterrose.peterrose.dto.request.OrderCreateDTO;
import com.peterrose.peterrose.dto.request.OrderUpdateStatusDTO;
import com.peterrose.peterrose.dto.response.OrderResponseDTO;
import com.peterrose.peterrose.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order REST controller - uses DTOs only
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    // GET /api/orders - Get all orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(status));
        }
        
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(orderService.searchOrders(search));
        }
        
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
    // GET /api/orders/{id} - Get single order (for tracking)
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/orders - Create new order (checkout)
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO createDTO) {
        OrderResponseDTO created = orderService.createOrder(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PATCH /api/orders/{id}/status - Update order status (admin)
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody OrderUpdateStatusDTO updateDTO
    ) {
        OrderResponseDTO updated = orderService.updateOrderStatus(id, updateDTO);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE /api/orders/{id} - Delete order (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
