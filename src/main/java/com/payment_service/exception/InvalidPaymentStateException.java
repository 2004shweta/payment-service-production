package com.payment_service.exception;

import com.payment_service.model.PaymentStatus;

public class InvalidPaymentStateException extends RuntimeException {

    public InvalidPaymentStateException(String message) {
        super(message);
    }

    public InvalidPaymentStateException(Long paymentId, PaymentStatus currentStatus, PaymentStatus targetStatus) {
        super(String.format("Cannot transition payment %d from %s to %s",
                paymentId, currentStatus, targetStatus));
    }
}