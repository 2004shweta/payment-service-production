package com.payment_service.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with id: " + paymentId);
    }

    public PaymentNotFoundException(String field, String value) {
        super(String.format("Payment not found with %s: %s", field, value));
    }
}