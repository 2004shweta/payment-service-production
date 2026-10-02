package com.payment_service.service;

import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.model.PaymentStatus;

import java.util.List;

public interface PaymentService {

    /**
     * Create a new payment
     */
    PaymentResponse createPayment(PaymentRequest request);

    /**
     * Get payment by ID
     */
    PaymentResponse getPaymentById(Long id);

    /**
     * Get payment by reference
     */
    PaymentResponse getPaymentByReference(String reference);

    /**
     * Get all payments for a merchant
     */
    List<PaymentResponse> getPaymentsByMerchant(String merchantId);

    /**
     * Get payments by merchant and status
     */
    List<PaymentResponse> getPaymentsByMerchantAndStatus(String merchantId, PaymentStatus status);

    /**
     * Process a payment (change status to PROCESSING and then COMPLETED/FAILED)
     */
    PaymentResponse processPayment(Long paymentId);

    /**
     * Retry a failed payment
     */
    PaymentResponse retryPayment(Long paymentId);

    /**
     * Cancel a payment
     */
    PaymentResponse cancelPayment(Long paymentId);
}