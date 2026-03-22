package com.peterrose.peterrose.service;

import com.peterrose.peterrose.dto.response.DailyPaymentSummaryDTO;
import com.peterrose.peterrose.dto.response.PaymentResponseDTO;

import java.util.Map;

/**
 * Payment service interface
 */
public interface PaymentService {

    /**
     * Create Yoco checkout session
     */
    Map<String, String> createYocoCheckout(
            String orderId,
            Double amount,
            String currency,
            String customerEmail,
            String successUrl,
            String cancelUrl
    );

    /**
     * Verify and save payment after Yoco redirect
     */
    PaymentResponseDTO verifyAndSavePayment(String yocoPaymentId, String orderId, Double amount);

    /**
     * Get payment by order ID
     */
    PaymentResponseDTO getPaymentByOrderId(String orderId);

    /**
     * Get payment by Yoco payment ID
     */
    PaymentResponseDTO getPaymentByYocoId(String yocoPaymentId);

    /**
     * Get a summary of all successful payments made today,
     * including the total amount collected
     */
    DailyPaymentSummaryDTO getTodaySuccessfulPaymentsSummary();
}