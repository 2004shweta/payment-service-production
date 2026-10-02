package com.payment_service.service;

import com.payment_service.model.Payment;
import com.payment_service.model.PaymentAudit;
import com.payment_service.model.PaymentStatus;
import com.payment_service.repository.PaymentAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentAuditService {

    private final PaymentAuditRepository auditRepository;

    /**
     * Log payment creation
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreation(Payment payment) {
        log.debug("Logging payment creation: {}", payment.getPaymentReference());

        PaymentAudit audit = PaymentAudit.builder()
                .paymentId(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .oldStatus(null)
                .newStatus(payment.getStatus())
                .action("CREATE")
                .details(String.format("Payment created with amount %s %s",
                        payment.getAmount(), payment.getCurrency()))
                .build();

        auditRepository.save(audit);
    }

    /**
     * Log status change
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logStatusChange(Payment payment, PaymentStatus oldStatus, PaymentStatus newStatus) {
        log.debug("Logging status change for payment {}: {} -> {}",
                payment.getPaymentReference(), oldStatus, newStatus);

        PaymentAudit audit = PaymentAudit.builder()
                .paymentId(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .action("STATUS_CHANGE")
                .details(String.format("Status changed from %s to %s", oldStatus, newStatus))
                .build();

        auditRepository.save(audit);
    }

    /**
     * Log payment failure
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(Payment payment, String reason) {
        log.debug("Logging payment failure: {}", payment.getPaymentReference());

        PaymentAudit audit = PaymentAudit.builder()
                .paymentId(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .oldStatus(PaymentStatus.PROCESSING)
                .newStatus(PaymentStatus.FAILED)
                .action("FAILURE")
                .details(String.format("Payment failed: %s", reason))
                .build();

        auditRepository.save(audit);
    }

    /**
     * Log retry attempt
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRetry(Payment payment) {
        log.debug("Logging retry for payment: {}", payment.getPaymentReference());

        PaymentAudit audit = PaymentAudit.builder()
                .paymentId(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .oldStatus(PaymentStatus.FAILED)
                .newStatus(PaymentStatus.PROCESSING)
                .action("RETRY")
                .details(String.format("Retry attempt #%d", payment.getRetryCount()))
                .build();

        auditRepository.save(audit);
    }

    /**
     * Get audit history for a payment
     */
    public List<PaymentAudit> getPaymentHistory(Long paymentId) {
        return auditRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId);
    }

    /**
     * Get audit history by payment reference
     */
    public List<PaymentAudit> getPaymentHistoryByReference(String paymentReference) {
        return auditRepository.findByPaymentReferenceOrderByCreatedAtDesc(paymentReference);
    }
}