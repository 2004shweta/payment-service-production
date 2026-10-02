package com.payment_service.dto;

import com.payment_service.model.Payment;

public class PaymentMapper {

    // Entity -> DTO
    public static PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .merchantId(payment.getMerchantId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .retryCount(payment.getRetryCount())
                .createdAt(payment.getCreatedAt())
                .processedAt(payment.getProcessedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }

    // Request -> Entity
    public static Payment toEntity(PaymentRequest request) {
        return Payment.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .merchantId(request.getMerchantId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .callbackUrl(request.getCallbackUrl())
                .retryCount(0)
                .build();
    }
}