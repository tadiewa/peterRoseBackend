package com.peterrose.peterrose.service.impl;

import com.peterrose.peterrose.constants.OrderStatus;
import com.peterrose.peterrose.constants.PaymentMethod;
import com.peterrose.peterrose.constants.PaymentStatus;
import com.peterrose.peterrose.dto.response.DailyPaymentSummaryDTO;
import com.peterrose.peterrose.dto.response.PaymentResponseDTO;
import com.peterrose.peterrose.exception.ResourceNotFoundException;
import com.peterrose.peterrose.model.*;
import com.peterrose.peterrose.repository.OrderRepository;
import com.peterrose.peterrose.repository.PaymentRepository;
import com.peterrose.peterrose.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Payment service implementation with Yoco Checkout API
 * Handles idempotent payment verification
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${yoco.secret.key:}")
    private String yocoSecretKey;

    @Override
    public Map<String, String> createYocoCheckout(
            String orderId,
            Double amount,
            String currency,
            String customerEmail,
            String successUrl,
            String cancelUrl) {

        log.info("Creating Yoco checkout for order: {} amount: R{}", orderId, amount);

        try {
            String yocoUrl = "https://payments.yoco.com/api/checkouts";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", convertToYocoCents(amount));
            requestBody.put("currency", currency);
            requestBody.put("successUrl", successUrl);
            requestBody.put("cancelUrl", cancelUrl);
            requestBody.put("failureUrl", cancelUrl);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("orderId", orderId);
            requestBody.put("metadata", metadata);

            if (customerEmail != null && !customerEmail.isEmpty()) {
                requestBody.put("customerEmail", customerEmail);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(yocoSecretKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Calling Yoco API: {}", yocoUrl);

            ResponseEntity<Map> response = restTemplate.exchange(
                    yocoUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                Map<String, Object> responseBody = response.getBody();
                String checkoutId = (String) responseBody.get("id");
                String redirectUrl = (String) responseBody.get("redirectUrl");

                log.info(" Yoco checkout created: {} for order: {}", checkoutId, orderId);

                Map<String, String> result = new HashMap<>();
                result.put("checkoutId", checkoutId);
                result.put("redirectUrl", redirectUrl);
                return result;
            }

            log.error(" Failed to create Yoco checkout. Response: {}", response);
            throw new RuntimeException("Failed to create Yoco checkout");

        } catch (Exception e) {
            log.error(" Error creating Yoco checkout: {}", e.getMessage(), e);
            throw new RuntimeException("Payment gateway error: " + e.getMessage());
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PaymentResponseDTO verifyAndSavePayment(String yocoPaymentId, String orderId, Double paidAmount) {
        log.info("Verifying payment: {} for order: {}", yocoPaymentId, orderId);

        // Use pessimistic lock to prevent concurrent access
        Optional<Payment> existingByOrder = paymentRepository.findByOrderId(orderId);
        if (existingByOrder.isPresent()) {
            log.info("✅ Payment already exists for order: {}", orderId);
            return toResponseDTO(existingByOrder.get());
        }

        Optional<Payment> existingByYoco = paymentRepository.findByYocoPaymentId(yocoPaymentId);
        if (existingByYoco.isPresent()) {
            log.info("✅ Payment with Yoco ID already exists");
            return toResponseDTO(existingByYoco.get());
        }
        Order order = findOrderOrThrow(orderId);

        // If amount is 0 or null, use order total
        if (paidAmount == null || paidAmount == 0) {
            paidAmount = order.getTotal();
            log.info("Using order total as payment amount: R{}", paidAmount);
        }

        // Validate amount matches order total
        validatePaymentAmount(order, paidAmount, orderId);

        // Create and save payment record
        Payment payment = createPaymentRecord(yocoPaymentId, orderId, paidAmount);
        Payment savedPayment = paymentRepository.save(payment);

        // Update order status to CONFIRMED
        confirmOrder(order);

        log.info(" Payment verified and saved: {} for order: {}", yocoPaymentId, orderId);
        return toResponseDTO(savedPayment);
    }

    @Override
    public PaymentResponseDTO getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return toResponseDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentByYocoId(String yocoPaymentId) {
        Payment payment = paymentRepository.findByYocoPaymentId(yocoPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "yocoPaymentId", yocoPaymentId));
        return toResponseDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public DailyPaymentSummaryDTO getTodaySuccessfulPaymentsSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        log.info("Fetching successful payments for: {}", today);

        List<Payment> payments = paymentRepository.findByStatusAndPaidAtBetween(
                PaymentStatus.SUCCEEDED, startOfDay, endOfDay
        );

        double totalAmount = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        List<PaymentResponseDTO> paymentDTOs = payments.stream()
                .map(this::toResponseDTO)
                .toList();

        log.info("Found {} successful payment(s) totalling R{} for {}", payments.size(), totalAmount, today);

        return DailyPaymentSummaryDTO.builder()
                .date(today)
                .paymentCount(payments.size())
                .totalAmount(totalAmount)
                .currency("ZAR")
                .payments(paymentDTOs)
                .build();
    }


    private Order findOrderOrThrow(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new ResourceNotFoundException("Order", "id", orderId);
                });
    }

    private void validatePaymentAmount(Order order, Double paidAmount, String orderId) {
        if (!order.getTotal().equals(paidAmount)) {
            log.error("SECURITY ALERT: Amount mismatch for order {}. Expected: R{}, Got: R{}",
                    orderId, order.getTotal(), paidAmount);
            throw new SecurityException("Payment amount does not match order total");
        }
    }

    private Payment checkForDuplicatePayment(String yocoPaymentId) {
        return paymentRepository.findByYocoPaymentId(yocoPaymentId).orElse(null);
    }

    private Payment createPaymentRecord(String yocoPaymentId, String orderId, Double amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setYocoPaymentId(yocoPaymentId);
        payment.setAmount(amount);
        payment.setCurrency("ZAR");
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setPaidAt(LocalDateTime.now());
        return payment;
    }

    private void confirmOrder(Order order) {
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.CONFIRMED);

            TrackingUpdate trackingUpdate = new TrackingUpdate();
            trackingUpdate.setStatus("Order Confirmed");
            trackingUpdate.setTimestamp(LocalDateTime.now());
            trackingUpdate.setDescription("Payment confirmed. Our florists are preparing your arrangement.");
            order.getTrackingUpdates().add(trackingUpdate);

            orderRepository.save(order);
            log.info(" Order confirmed: {}", order.getId());
        }
    }

    private Integer convertToYocoCents(Double amount) {
        return (int) Math.round(amount * 100);
    }

    private PaymentResponseDTO toResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .yocoPaymentId(payment.getYocoPaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().getValue())
                .paymentMethod(payment.getPaymentMethod() != null ?
                        payment.getPaymentMethod().getValue() : null)
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}