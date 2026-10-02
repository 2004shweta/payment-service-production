package com.payment_service.service;

import com.payment_service.dto.PaymentMapper;
import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.exception.DuplicatePaymentException;
import com.payment_service.exception.InvalidPaymentStateException;
import com.payment_service.exception.PaymentNotFoundException;
import com.payment_service.exception.PaymentProcessingException;
import com.payment_service.model.IdempotencyKey;
import com.payment_service.model.Payment;
import com.payment_service.model.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.payment_service.util.PaymentReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyService idempotencyService;
    private final PaymentAuditService auditService;
    private final PaymentEventPublisher eventPublisher;
    private static final int MAX_RETRY_COUNT = 3;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating payment for merchant: {}, amount: {} {}",
                request.getMerchantId(), request.getAmount(), request.getCurrency());

        // Check for duplicate payment using idempotency key
        Optional<IdempotencyKey> existingKey =
                idempotencyService.checkIdempotencyKey(request.getIdempotencyKey());

        if (existingKey.isPresent()) {
            // Return existing payment
            log.info("Duplicate request detected, returning existing payment: {}",
                    existingKey.get().getPaymentReference());

            Payment existingPayment = paymentRepository
                    .findByPaymentReference(existingKey.get().getPaymentReference())
                    .orElseThrow(() -> new PaymentNotFoundException(
                            "paymentReference", existingKey.get().getPaymentReference()));

            throw new DuplicatePaymentException(
                    request.getIdempotencyKey(),
                    existingPayment.getPaymentReference()
            );
        }

        // Create new payment
        Payment payment = PaymentMapper.toEntity(request);
        payment.setPaymentReference(PaymentReferenceGenerator.generate());
        payment.setStatus(PaymentStatus.PENDING);

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);

        // Store idempotency key
        idempotencyService.storeIdempotencyKey(savedPayment);

        // Log creation
        auditService.logCreation(savedPayment);

        // Publish event
        eventPublisher.publishPaymentCreated(savedPayment);

        log.info("Payment created successfully: {}", savedPayment.getPaymentReference());

        return PaymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "payments", key = "#id")
    public PaymentResponse getPaymentById(Long id) {
        log.debug("Fetching payment by id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "payments", key = "'ref:' + #reference")
    public PaymentResponse getPaymentByReference(String reference) {
        log.debug("Fetching payment by reference: {}", reference);

        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new PaymentNotFoundException("paymentReference", reference));

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMerchant(String merchantId) {
        log.debug("Fetching payments for merchant: {}", merchantId);

        List<Payment> payments = paymentRepository.findByMerchantId(merchantId);

        return payments.stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMerchantAndStatus(String merchantId, PaymentStatus status) {
        log.debug("Fetching payments for merchant: {} with status: {}", merchantId, status);

        List<Payment> payments = paymentRepository.findByMerchantIdAndStatus(merchantId, status);

        return payments.stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "payments", allEntries = true)
    public PaymentResponse processPayment(Long paymentId) {
        log.info("Processing payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        // Validate payment can be processed
        if (payment.getStatus() != PaymentStatus.PENDING &&
                payment.getStatus() != PaymentStatus.FAILED) {
            throw new InvalidPaymentStateException(
                    paymentId, payment.getStatus(), PaymentStatus.PROCESSING
            );
        }

        PaymentStatus oldStatus = payment.getStatus();

        // Mark as processing
        payment.markAsProcessing();
        Payment processingPayment = paymentRepository.save(payment);
        auditService.logStatusChange(processingPayment, oldStatus, PaymentStatus.PROCESSING);

        // Simulate payment processing
        boolean isSuccessful = simulatePaymentProcessing(payment);

        if (isSuccessful) {
            payment.markAsCompleted();
            Payment completedPayment = paymentRepository.save(payment);
            auditService.logStatusChange(completedPayment, PaymentStatus.PROCESSING, PaymentStatus.COMPLETED);
            eventPublisher.publishPaymentCompleted(completedPayment); // YENİ EKLE
            log.info("Payment processed successfully: {}", payment.getPaymentReference());
        } else {
            String failureReason = "Payment processing failed - Insufficient funds";
            payment.markAsFailed(failureReason);
            Payment failedPayment = paymentRepository.save(payment);
            auditService.logFailure(failedPayment, failureReason);
            eventPublisher.publishPaymentFailed(failedPayment); // YENİ EKLE
            log.warn("Payment processing failed: {}", payment.getPaymentReference());
        }

        log.debug("Evicting cache for payment: {}", paymentId);

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse retryPayment(Long paymentId) {
        log.info("Retrying payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        // Validate payment can be retried
        if (payment.getStatus() != PaymentStatus.FAILED) {
            throw new InvalidPaymentStateException(
                    "Cannot retry payment that is not in FAILED status. Current status: " + payment.getStatus()
            );
        }

        // Check retry count
        if (payment.getRetryCount() >= MAX_RETRY_COUNT) {
            throw new PaymentProcessingException(
                    String.format("Maximum retry attempts (%d) exceeded for payment: %s",
                            MAX_RETRY_COUNT, payment.getPaymentReference())
            );
        }

        // Increment retry count
        payment.incrementRetryCount();
        auditService.logRetry(payment);

        // Retry processing
        return processPayment(paymentId);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        // Validate payment can be cancelled
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new InvalidPaymentStateException(
                    "Cannot cancel a completed payment"
            );
        }

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new InvalidPaymentStateException(
                    "Payment is already cancelled"
            );
        }

        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(PaymentStatus.CANCELLED);
        Payment cancelledPayment = paymentRepository.save(payment);

        auditService.logStatusChange(cancelledPayment, oldStatus, PaymentStatus.CANCELLED);

        // Publish event
        eventPublisher.publishPaymentCancelled(cancelledPayment);

        log.info("Payment cancelled successfully: {}", payment.getPaymentReference());

        return PaymentMapper.toResponse(cancelledPayment);
    }

    /**
     * Simulate payment processing
     * In real world, this would call external payment gateway
     */
    private boolean simulatePaymentProcessing(Payment payment) {
        try {
            // Simulate processing delay
            Thread.sleep(100);

            // Simulate 80% success rate
            return Math.random() < 0.8;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("Payment processing interrupted", e);
        }
    }
}