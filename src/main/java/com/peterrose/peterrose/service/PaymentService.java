package com.peterrose.peterrose.service;

import com.peterrose.peterrose.dto.response.DailyPaymentSummaryDTO;
import com.peterrose.peterrose.dto.response.PaymentResponseDTO;

import java.util.Map;

public interface PaymentService {
    Map<String, String> createYocoCheckout(
            String orderId,
            Double amount,
            String currency,
            String customerEmail,
            String successUrl,
            String cancelUrl
    );
    PaymentResponseDTO verifyAndSavePayment(String yocoPaymentId, String orderId, Double amount);
    PaymentResponseDTO getPaymentByOrderId(String orderId);
    PaymentResponseDTO getPaymentByYocoId(String yocoPaymentId);
    DailyPaymentSummaryDTO getTodaySuccessfulPaymentsSummary();
}