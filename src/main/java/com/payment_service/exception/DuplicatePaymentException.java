package com.payment_service.exception;

public class DuplicatePaymentException extends RuntimeException {

    private final String paymentReference;

    public DuplicatePaymentException(String idempotencyKey, String paymentReference) {
        super(String.format("Payment with idempotency key '%s' already exists. Payment reference: %s",
                idempotencyKey, paymentReference));
        this.paymentReference = paymentReference;
    }

    public String getPaymentReference() {
        return paymentReference;
    }
}