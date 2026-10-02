package com.payment_service.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent implements Serializable {

    private Long paymentId;
    private String paymentReference;
    private String merchantId;
    private String customerId;
    private BigDecimal amount;
    private Currency currency;
    private PaymentStatus status;
    private String description;
    private String callbackUrl;
    private LocalDateTime timestamp;

    // For failed payments
    private String failureReason;

    // Event metadata
    private String eventType; // CREATED, COMPLETED, FAILED, CANCELLED
}