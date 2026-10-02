package com.payment_service.service;

import com.payment_service.config.RabbitMQConfig;
import com.payment_service.model.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final NotificationService notificationService;

    /**
     * Listen to payment created events
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_CREATED_QUEUE)
    public void handlePaymentCreated(PaymentEvent event) {
        log.info("Received payment created event: {}", event.getPaymentReference());

        try {
            notificationService.sendPaymentCreatedNotification(event);
            log.debug("Payment created notification sent successfully");
        } catch (Exception e) {
            log.error("Error processing payment created event: {}", event.getPaymentReference(), e);
            // In production, consider DLQ (Dead Letter Queue) for failed messages
        }
    }

    /**
     * Listen to payment completed events
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentEvent event) {
        log.info("Received payment completed event: {}", event.getPaymentReference());

        try {
            notificationService.sendPaymentCompletedNotification(event);
            log.debug("Payment completed notification sent successfully");
        } catch (Exception e) {
            log.error("Error processing payment completed event: {}", event.getPaymentReference(), e);
        }
    }

    /**
     * Listen to payment failed events
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE)
    public void handlePaymentFailed(PaymentEvent event) {
        log.info("Received payment failed event: {}", event.getPaymentReference());

        try {
            notificationService.sendPaymentFailedNotification(event);
            log.debug("Payment failed notification sent successfully");
        } catch (Exception e) {
            log.error("Error processing payment failed event: {}", event.getPaymentReference(), e);
        }
    }

    /**
     * Listen to payment cancelled events
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_CANCELLED_QUEUE)
    public void handlePaymentCancelled(PaymentEvent event) {
        log.info("Received payment cancelled event: {}", event.getPaymentReference());

        try {
            notificationService.sendPaymentCancelledNotification(event);
            log.debug("Payment cancelled notification sent successfully");
        } catch (Exception e) {
            log.error("Error processing payment cancelled event: {}", event.getPaymentReference(), e);
        }
    }
}