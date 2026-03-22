package com.peterrose.peterrose.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Summary of successful payments made on a given day
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPaymentSummaryDTO {

    private LocalDate date;
    private int paymentCount;
    private Double totalAmount;
    private String currency;
    private List<PaymentResponseDTO> payments;
}
