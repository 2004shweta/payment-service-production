package com.payment_service.service;

import com.payment_service.dto.PaymentMapper;
import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.exception.DuplicatePaymentException;
import com.payment_service.exception.InvalidPaymentStateException;
import com.payment_service.exception.PaymentNotFoundException;
import com.payment_service.model.*;
import com.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private PaymentAuditService auditService;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest testRequest;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testRequest = PaymentRequest.builder()
                .idempotencyKey("test-key-123")
                .merchantId("merchant-001")
                .customerId("customer-001")
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .description("Test payment")
                .callbackUrl("https://example.com/webhook")
                .build();

        testPayment = Payment.builder()
                .paymentReference("PAY-123456")
                .idempotencyKey("test-key-123")
                .merchantId("merchant-001")
                .customerId("customer-001")
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .status(PaymentStatus.PENDING)
                .description("Test payment")
                .callbackUrl("https://example.com/webhook")
                .retryCount(0)
                .build();
    }

    @Test
    void createPayment_Success() {
        // Given
        when(idempotencyService.checkIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.createPayment(testRequest);

        // Then
        assertNotNull(response);
        assertEquals(testPayment.getPaymentReference(), response.getPaymentReference());
        assertEquals(PaymentStatus.PENDING, response.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(idempotencyService, times(1)).storeIdempotencyKey(any(Payment.class));
        verify(auditService, times(1)).logCreation(any(Payment.class));
        verify(eventPublisher, times(1)).publishPaymentCreated(any(Payment.class));
    }

    @Test
    void createPayment_DuplicateIdempotencyKey_ThrowsException() {
        // Given
        IdempotencyKey existingKey = IdempotencyKey.builder()
                .idempotencyKey("test-key-123")
                .paymentReference("PAY-EXISTING")
                .build();

        when(idempotencyService.checkIdempotencyKey(anyString())).thenReturn(Optional.of(existingKey));
        when(paymentRepository.findByPaymentReference(anyString())).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThrows(DuplicatePaymentException.class, () -> {
            paymentService.createPayment(testRequest);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void getPaymentById_Success() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When
        PaymentResponse response = paymentService.getPaymentById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testPayment.getPaymentReference(), response.getPaymentReference());
    }

    @Test
    void getPaymentById_NotFound_ThrowsException() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPaymentById(1L);
        });
    }

    @Test
    void processPayment_Success() {
        // Given
        testPayment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.processPayment(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getStatus() == PaymentStatus.COMPLETED || response.getStatus() == PaymentStatus.FAILED);

        verify(paymentRepository, atLeast(1)).save(any(Payment.class));
        verify(auditService, atLeast(1)).logStatusChange(any(Payment.class), any(), any());
    }

    @Test
    void processPayment_InvalidState_ThrowsException() {
        // Given
        testPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThrows(InvalidPaymentStateException.class, () -> {
            paymentService.processPayment(1L);
        });
    }

    @Test
    void cancelPayment_Success() {
        // Given
        testPayment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.cancelPayment(1L);

        // Then
        assertNotNull(response);
        assertEquals(PaymentStatus.CANCELLED, response.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(auditService, times(1)).logStatusChange(any(Payment.class), any(), any());
        verify(eventPublisher, times(1)).publishPaymentCancelled(any(Payment.class));
    }

    @Test
    void cancelPayment_AlreadyCompleted_ThrowsException() {
        // Given
        testPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThrows(InvalidPaymentStateException.class, () -> {
            paymentService.cancelPayment(1L);
        });
    }
}