package com.peterrose.peterrose.repository;

import com.peterrose.peterrose.model.Order;
import com.peterrose.peterrose.constants.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    // Find orders by status
    List<Order> findByStatus(OrderStatus status);
    
    // Find orders by customer email
    List<Order> findByCustomer_Email(String email);
    
    // Search orders by ID or customer name
    @Query("SELECT o FROM Order o WHERE " +
           "LOWER(o.id) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.customer.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.customer.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.customer.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Order> searchOrders(@Param("query") String query);
    
    // Find recent orders (last N orders)
    List<Order> findTop10ByOrderByCreatedAtDesc();
}
