package com.payment_service.repository;


import com.payment_service.model.Payment;
import com.payment_service.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find by payment reference
    Optional<Payment> findByPaymentReference(String paymentReference);

    // Find by idempotency key
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    // Find by merchant ID
    List<Payment> findByMerchantId(String merchantId);

    // Find by merchant ID and status
    List<Payment> findByMerchantIdAndStatus(String merchantId, PaymentStatus status);

    // Find by status
    List<Payment> findByStatus(PaymentStatus status);

    // Find failed payments with retry count less than max
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.retryCount < :maxRetries")
    List<Payment> findFailedPaymentsForRetry(@Param("maxRetries") int maxRetries);

    // Find pending payments older than specified time
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findStalePendingPayments(@Param("cutoffTime") LocalDateTime cutoffTime);

    // Check if payment exists by idempotency key
    boolean existsByIdempotencyKey(String idempotencyKey);
}