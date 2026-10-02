package com.payment_service.service;


import com.payment_service.model.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final WebhookService webhookService;

    @Override
    public void sendPaymentCreatedNotification(PaymentEvent event) {
        log.info("Sending payment created notification: {}", event.getPaymentReference());

        // Simulate sending email/SMS (in real app, integrate with email/SMS provider)
        simulateSendEmail(
                event.getCustomerId(),
                "Payment Created",
                String.format("Your payment of %s %s has been created. Reference: %s",
                        event.getAmount(), event.getCurrency(), event.getPaymentReference())
        );

        // Send webhook if callback URL is provided
        if (event.getCallbackUrl() != null && !event.getCallbackUrl().isEmpty()) {
            webhookService.sendWebhook(event);
        }
    }

    @Override
    public void sendPaymentCompletedNotification(PaymentEvent event) {
        log.info("Sending payment completed notification: {}", event.getPaymentReference());

        // Simulate sending email/SMS
        simulateSendEmail(
                event.getCustomerId(),
                "Payment Completed",
                String.format("Your payment of %s %s has been completed successfully. Reference: %s",
                        event.getAmount(), event.getCurrency(), event.getPaymentReference())
        );

        // Send webhook
        if (event.getCallbackUrl() != null && !event.getCallbackUrl().isEmpty()) {
            webhookService.sendWebhook(event);
        }
    }

    @Override
    public void sendPaymentFailedNotification(PaymentEvent event) {
        log.info("Sending payment failed notification: {}", event.getPaymentReference());

        // Simulate sending email/SMS
        simulateSendEmail(
                event.getCustomerId(),
                "Payment Failed",
                String.format("Your payment of %s %s has failed. Reference: %s. Reason: %s",
                        event.getAmount(), event.getCurrency(), event.getPaymentReference(),
                        event.getFailureReason())
        );

        // Send webhook
        if (event.getCallbackUrl() != null && !event.getCallbackUrl().isEmpty()) {
            webhookService.sendWebhook(event);
        }
    }

    @Override
    public void sendPaymentCancelledNotification(PaymentEvent event) {
        log.info("Sending payment cancelled notification: {}", event.getPaymentReference());

        // Simulate sending email/SMS
        simulateSendEmail(
                event.getCustomerId(),
                "Payment Cancelled",
                String.format("Your payment of %s %s has been cancelled. Reference: %s",
                        event.getAmount(), event.getCurrency(), event.getPaymentReference())
        );

        // Send webhook
        if (event.getCallbackUrl() != null && !event.getCallbackUrl().isEmpty()) {
            webhookService.sendWebhook(event);
        }
    }

    /**
     * Simulate sending email (in real app, use email provider like SendGrid, AWS SES, etc.)
     */
    private void simulateSendEmail(String recipient, String subject, String body) {
        log.info("EMAIL SENT - To: {}, Subject: {}, Body: {}", recipient, subject, body);

        // In real application:
        // emailProvider.send(recipient, subject, body);
    }
}