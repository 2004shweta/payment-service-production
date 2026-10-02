package com.payment_service.service;

import com.payment_service.model.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 second

    /**
     * Send webhook notification with retry logic
     */
    public void sendWebhook(PaymentEvent event) {
        if (event.getCallbackUrl() == null || event.getCallbackUrl().isEmpty()) {
            log.debug("No callback URL provided for payment: {}", event.getPaymentReference());
            return;
        }

        log.info("Sending webhook to: {} for payment: {}",
                event.getCallbackUrl(), event.getPaymentReference());

        int attempt = 0;
        boolean success = false;

        while (attempt < MAX_RETRY_ATTEMPTS && !success) {
            attempt++;

            try {
                sendWebhookRequest(event);
                success = true;
                log.info("Webhook sent successfully on attempt {}: {}", attempt, event.getPaymentReference());

            } catch (Exception e) {
                log.error("Webhook failed on attempt {}/{} for payment: {}. Error: {}",
                        attempt, MAX_RETRY_ATTEMPTS, event.getPaymentReference(), e.getMessage());

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Webhook retry interrupted");
                        break;
                    }
                } else {
                    log.error("Webhook failed after {} attempts for payment: {}",
                            MAX_RETRY_ATTEMPTS, event.getPaymentReference());
                }
            }
        }
    }

    /**
     * Send HTTP POST request to webhook URL
     */
    private void sendWebhookRequest(PaymentEvent event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Add signature header for webhook verification (in real app, use HMAC)
        headers.set("X-Payment-Signature", generateSignature(event));
        headers.set("X-Payment-Event", event.getEventType());

        HttpEntity<PaymentEvent> request = new HttpEntity<>(event, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                event.getCallbackUrl(),
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Webhook returned non-2xx status: " + response.getStatusCode());
        }
    }

    /**
     * Generate signature for webhook verification
     * In real app, use HMAC-SHA256 with secret key
     */
    private String generateSignature(PaymentEvent event) {
        // Simplified signature (in real app, use proper HMAC)
        return String.format("sha256=%s",
                Integer.toHexString((event.getPaymentReference() + event.getEventType()).hashCode()));
    }
}