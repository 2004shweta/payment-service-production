package com.payment_service.service;

import com.payment_service.config.RabbitMQConfig;
import com.payment_service.model.Payment;
import com.payment_service.model.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publish payment created event
     */
    public void publishPaymentCreated(Payment payment) {
        log.info("Publishing payment created event: {}", payment.getPaymentReference());

        PaymentEvent event = buildEvent(payment, "CREATED");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_CREATED_ROUTING_KEY,
                event
        );

        log.debug("Payment created event published successfully");
    }

    /**
     * Publish payment completed event
     */
    public void publishPaymentCompleted(Payment payment) {
        log.info("Publishing payment completed event: {}", payment.getPaymentReference());

        PaymentEvent event = buildEvent(payment, "COMPLETED");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                event
        );

        log.debug("Payment completed event published successfully");
    }

    /**
     * Publish payment failed event
     */
    public void publishPaymentFailed(Payment payment) {
        log.info("Publishing payment failed event: {}", payment.getPaymentReference());

        PaymentEvent event = buildEvent(payment, "FAILED");
        event.setFailureReason(payment.getFailureReason());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_FAILED_ROUTING_KEY,
                event
        );

        log.debug("Payment failed event published successfully");
    }

    /**
     * Publish payment cancelled event
     */
    public void publishPaymentCancelled(Payment payment) {
        log.info("Publishing payment cancelled event: {}", payment.getPaymentReference());

        PaymentEvent event = buildEvent(payment, "CANCELLED");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_CANCELLED_ROUTING_KEY,
                event
        );

        log.debug("Payment cancelled event published successfully");
    }

    /**
     * Build payment event from payment entity
     */
    private PaymentEvent buildEvent(Payment payment, String eventType) {
        return PaymentEvent.builder()
                .paymentId(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .merchantId(payment.getMerchantId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .description(payment.getDescription())
                .callbackUrl(payment.getCallbackUrl())
                .timestamp(LocalDateTime.now())
                .eventType(eventType)
                .build();
    }
}