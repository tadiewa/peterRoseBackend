package com.peterrose.peterrose.service.impl;

import com.peterrose.peterrose.constants.DeliveryMethod;
import com.peterrose.peterrose.constants.OrderStatus;
import com.peterrose.peterrose.dto.request.OrderCreateDTO;
import com.peterrose.peterrose.dto.request.OrderItemCreateDTO;
import com.peterrose.peterrose.dto.request.OrderUpdateStatusDTO;
import com.peterrose.peterrose.dto.response.OrderResponseDTO;
import com.peterrose.peterrose.exception.ResourceNotFoundException;
import com.peterrose.peterrose.mapper.OrderMapper;
import com.peterrose.peterrose.model.*;
import com.peterrose.peterrose.repository.OrderRepository;
import com.peterrose.peterrose.repository.ProductRepository;
import com.peterrose.peterrose.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Order service implementation
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    // Business constants
    private static final Double DELIVERY_FEE = 150.0;

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseDTOList(orders);
    }

    @Override
    public Optional<OrderResponseDTO> getOrderById(String id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponseDTO);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        OrderStatus orderStatus = OrderStatus.fromValue(status);
        List<Order> orders = orderRepository.findByStatus(orderStatus);
        return orderMapper.toResponseDTOList(orders);
    }

    @Override
    public List<OrderResponseDTO> searchOrders(String query) {
        List<Order> orders = orderRepository.searchOrders(query);
        return orderMapper.toResponseDTOList(orders);
    }

    @Override
    public OrderResponseDTO createOrder(OrderCreateDTO createDTO) {
        // Generate order ID
        String orderId = generateOrderId();

        // Create order entity
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        // Set delivery details
        DeliveryMethod deliveryMethod = DeliveryMethod.fromValue(createDTO.getDeliveryMethod());
        order.setDeliveryMethod(deliveryMethod);
        order.setDeliveryDate(createDTO.getDeliveryDate());
        order.setDeliveryTime(createDTO.getDeliveryTime());
        order.setCollectionPointId(createDTO.getCollectionPointId());

        // Set customer
        Customer customer = orderMapper.toCustomerEntity(createDTO.getCustomer());
        order.setCustomer(customer);

        // Process order items and calculate subtotal
        List<OrderItem> orderItems = new ArrayList<>();
        double subtotal = 0.0;

        for (OrderItemCreateDTO itemDTO : createDTO.getItems()) {
            // Find product
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));

            // Check stock availability
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Create order item
            OrderItem orderItem = orderMapper.toOrderItemEntity(itemDTO, product);
            orderItems.add(orderItem);

            // Calculate subtotal
            subtotal += product.getPrice() * itemDTO.getQuantity();

            // Reduce stock
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);
        }

        order.setItems(orderItems);
        order.setSubtotal(subtotal);

        // Calculate delivery cost (backend calculates if frontend doesn't provide)
        Double deliveryCost = calculateDeliveryCost(createDTO.getDeliveryCost(), deliveryMethod);
        order.setDeliveryCost(deliveryCost);
        order.setTotal(subtotal + deliveryCost);

        // Add initial tracking update
        TrackingUpdate initialTracking = new TrackingUpdate();
        initialTracking.setStatus("Order Placed");
        initialTracking.setTimestamp(LocalDateTime.now());
        initialTracking.setDescription("Your order has been received and is being processed.");

        List<TrackingUpdate> trackingUpdates = new ArrayList<>();
        trackingUpdates.add(initialTracking);
        order.setTrackingUpdates(trackingUpdates);

        // Save order
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(String id, OrderUpdateStatusDTO updateDTO) {
        // Find order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // Update status
        OrderStatus newStatus = OrderStatus.fromValue(updateDTO.getStatus());
        order.setStatus(newStatus);

        // Add tracking update
        TrackingUpdate trackingUpdate = new TrackingUpdate();
        trackingUpdate.setStatus(getStatusDisplayName(newStatus));
        trackingUpdate.setTimestamp(LocalDateTime.now());
        trackingUpdate.setDescription(updateDTO.getDescription() != null
                ? updateDTO.getDescription()
                : getDefaultStatusDescription(newStatus));

        order.getTrackingUpdates().add(trackingUpdate);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(updatedOrder);
    }

    @Override
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        orderRepository.deleteById(id);
    }


    private String generateOrderId() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long count = orderRepository.count() + 1;
        return String.format("ORD-%s-%03d", year, count);
    }

    private String getStatusDisplayName(OrderStatus status) {
        switch (status) {
            case PENDING: return "Order Placed";
            case CONFIRMED: return "Order Confirmed";
            case PREPARING: return "Being Prepared";
            case OUT_FOR_DELIVERY: return "Out for Delivery";
            case DELIVERED: return "Delivered";
            case CANCELLED: return "Order Cancelled";
            case READY_FOR_COLLECTION: return "Ready for Collection";
            default: return status.getValue();
        }
    }

    /**
     * Get default description for status
     */
    private String getDefaultStatusDescription(OrderStatus status) {
        switch (status) {
            case PENDING:
                return "Your order has been received and is being processed.";
            case CONFIRMED:
                return "Payment confirmed. Our florists are preparing your arrangement.";
            case PREPARING:
                return "Your beautiful flowers are being arranged with care.";
            case OUT_FOR_DELIVERY:
                return "Your order is on its way to you!";
            case DELIVERED:
                return "Your order has been delivered. Enjoy your flowers!";
            case CANCELLED:
                return "Your order has been cancelled.";
            case READY_FOR_COLLECTION:
                return "Your order is ready for collection at the selected pickup point.";
            default:
                return "Order status updated.";
        }
    }

    private Double calculateDeliveryCost(Double providedCost, DeliveryMethod deliveryMethod) {
        if (providedCost != null) {
            return providedCost;
        }
        return deliveryMethod == DeliveryMethod.DELIVERY ? DELIVERY_FEE : 0.0;
    }
}