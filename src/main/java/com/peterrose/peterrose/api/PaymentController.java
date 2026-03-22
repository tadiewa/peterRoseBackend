package com.peterrose.peterrose.api;

import com.peterrose.peterrose.dto.response.DailyPaymentSummaryDTO;
import com.peterrose.peterrose.dto.response.PaymentResponseDTO;
import com.peterrose.peterrose.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Payment Controller - Thin layer, delegates to service
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/create-checkout")
    public ResponseEntity<Map<String, String>> createCheckout(@RequestBody Map<String, Object> request) {
        String orderId = (String) request.get("orderId");
        Double amount = Double.parseDouble(request.get("amount").toString());
        String currency = (String) request.get("currency");
        String customerEmail = (String) request.get("customerEmail");
        String successUrl = (String) request.get("successUrl");
        String cancelUrl = (String) request.get("cancelUrl");

        log.info("Creating checkout for order: {}", orderId);

        Map<String, String> checkout = paymentService.createYocoCheckout(
                orderId, amount, currency, customerEmail, successUrl, cancelUrl
        );

        return ResponseEntity.ok(checkout);
    }


    @PostMapping("/verify")
    public ResponseEntity<PaymentResponseDTO> verifyPayment(@RequestBody Map<String, Object> request) {
        String yocoPaymentId = (String) request.get("yocoPaymentId");
        String orderId = (String) request.get("orderId");
        Double amount = Double.parseDouble(request.get("amount").toString());

        PaymentResponseDTO payment = paymentService.verifyAndSavePayment(yocoPaymentId, orderId, amount);
        return ResponseEntity.ok(payment);
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable String orderId) {
        PaymentResponseDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }


    @GetMapping("/yoco/{yocoPaymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByYocoId(@PathVariable String yocoPaymentId) {
        PaymentResponseDTO payment = paymentService.getPaymentByYocoId(yocoPaymentId);
        return ResponseEntity.ok(payment);
    }


    @GetMapping("/summary/today")
    public ResponseEntity<DailyPaymentSummaryDTO> getTodayPaymentsSummary() {
        log.info("Request received: GET /api/payments/summary/today");
        DailyPaymentSummaryDTO summary = paymentService.getTodaySuccessfulPaymentsSummary();
        return ResponseEntity.ok(summary);
    }
}