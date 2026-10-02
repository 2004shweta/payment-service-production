package com.payment_service.service;

import com.payment_service.model.PaymentEvent;

public interface NotificationService {

    /**
     * Send payment created notification
     */
    void sendPaymentCreatedNotification(PaymentEvent event);

    /**
     * Send payment completed notification
     */
    void sendPaymentCompletedNotification(PaymentEvent event);

    /**
     * Send payment failed notification
     */
    void sendPaymentFailedNotification(PaymentEvent event);

    /**
     * Send payment cancelled notification
     */
    void sendPaymentCancelledNotification(PaymentEvent event);
}