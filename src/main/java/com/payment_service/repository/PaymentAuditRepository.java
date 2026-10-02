package com.payment_service.repository;

import com.payment_service.model.PaymentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentAuditRepository extends JpaRepository<PaymentAudit, Long> {

    // Find all audit records for a payment
    List<PaymentAudit> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    // Find all audit records by payment reference
    List<PaymentAudit> findByPaymentReferenceOrderByCreatedAtDesc(String paymentReference);
}