package com.payment_service.repository;

import com.payment_service.config.TestJpaConfig;
import com.payment_service.model.Currency;
import com.payment_service.model.Payment;
import com.payment_service.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();

        testPayment = Payment.builder()
                .paymentReference("PAY-TEST-001")
                .idempotencyKey("idem-key-001")
                .merchantId("merchant-001")
                .customerId("customer-001")
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .status(PaymentStatus.PENDING)
                .description("Test payment")
                .build();
    }

    @Test
    void save_ValidPayment_Success() {
        // When
        Payment saved = paymentRepository.save(testPayment);

        // Then
        assertNotNull(saved.getId());
        assertEquals(testPayment.getPaymentReference(), saved.getPaymentReference());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findByPaymentReference_ExistingPayment_ReturnsPayment() {
        // Given
        paymentRepository.save(testPayment);

        // When
        Optional<Payment> found = paymentRepository.findByPaymentReference("PAY-TEST-001");

        // Then
        assertTrue(found.isPresent());
        assertEquals(testPayment.getPaymentReference(), found.get().getPaymentReference());
    }

    @Test
    void findByIdempotencyKey_ExistingPayment_ReturnsPayment() {
        // Given
        paymentRepository.save(testPayment);

        // When
        Optional<Payment> found = paymentRepository.findByIdempotencyKey("idem-key-001");

        // Then
        assertTrue(found.isPresent());
        assertEquals(testPayment.getIdempotencyKey(), found.get().getIdempotencyKey());
    }

    @Test
    void findByMerchantId_MultiplePayments_ReturnsAll() {
        // Given
        paymentRepository.save(testPayment);

        Payment payment2 = Payment.builder()
                .paymentReference("PAY-TEST-002")
                .idempotencyKey("idem-key-002")
                .merchantId("merchant-001")
                .amount(new BigDecimal("50.00"))
                .currency(Currency.EUR)
                .status(PaymentStatus.COMPLETED)
                .build();
        paymentRepository.save(payment2);

        // When
        List<Payment> payments = paymentRepository.findByMerchantId("merchant-001");

        // Then
        assertEquals(2, payments.size());
    }

    @Test
    void findByStatus_FilteredByStatus_ReturnsMatching() {
        // Given
        paymentRepository.save(testPayment);

        Payment completedPayment = Payment.builder()
                .paymentReference("PAY-TEST-003")
                .idempotencyKey("idem-key-003")
                .merchantId("merchant-002")
                .amount(new BigDecimal("75.00"))
                .currency(Currency.GBP)
                .status(PaymentStatus.COMPLETED)
                .build();
        paymentRepository.save(completedPayment);

        // When
        List<Payment> pendingPayments = paymentRepository.findByStatus(PaymentStatus.PENDING);

        // Then
        assertEquals(1, pendingPayments.size());
        assertEquals(PaymentStatus.PENDING, pendingPayments.get(0).getStatus());
    }

    @Test
    void existsByIdempotencyKey_ExistingKey_ReturnsTrue() {
        // Given
        paymentRepository.save(testPayment);

        // When
        boolean exists = paymentRepository.existsByIdempotencyKey("idem-key-001");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByIdempotencyKey_NonExistingKey_ReturnsFalse() {
        // When
        boolean exists = paymentRepository.existsByIdempotencyKey("non-existing-key");

        // Then
        assertFalse(exists);
    }
}