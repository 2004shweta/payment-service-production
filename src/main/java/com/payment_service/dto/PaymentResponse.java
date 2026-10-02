package com.payment_service.dto;

import com.payment_service.model.Currency;
import com.payment_service.model.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private String paymentReference;
    private String merchantId;
    private String customerId;
    private BigDecimal amount;
    private Currency currency;
    private PaymentStatus status;
    private String description;
    private String failureReason;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime completedAt;
}