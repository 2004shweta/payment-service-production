package com.payment_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment_service.config.TestMessagingConfig;
import com.payment_service.config.TestRedisConfig;
import com.payment_service.dto.PaymentRequest;
import com.payment_service.model.Currency;
import com.payment_service.model.Payment;
import com.payment_service.model.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.payment_service.repository.IdempotencyKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestMessagingConfig.class, TestRedisConfig.class})
@Transactional
@Disabled("Integration tests disabled")
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private IdempotencyKeyRepository idempotencyKeyRepository;

    private PaymentRequest validRequest;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        paymentRepository.deleteAll();
        idempotencyKeyRepository.deleteAll();

        validRequest = PaymentRequest.builder()
                .idempotencyKey("test-key-" + System.currentTimeMillis())
                .merchantId("merchant-001")
                .customerId("customer-001")
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .description("Integration test payment")
                .callbackUrl("https://example.com/webhook")
                .build();
    }

    @Test
    void createPayment_ValidRequest_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentReference").exists())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.amount").value(100.00))
                .andExpect(jsonPath("$.data.currency").value("USD"));
    }

    @Test
    void createPayment_InvalidAmount_ReturnsBadRequest() throws Exception {
        validRequest.setAmount(new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void createPayment_MissingMerchantId_ReturnsBadRequest() throws Exception {
        validRequest.setMerchantId(null);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void getPaymentById_ExistingPayment_ReturnsPayment() throws Exception {
        // Create a payment first
        Payment payment = Payment.builder()
                .paymentReference("PAY-TEST-123")
                .idempotencyKey("test-key-get")
                .merchantId("merchant-001")
                .amount(new BigDecimal("50.00"))
                .currency(Currency.EUR)
                .status(PaymentStatus.PENDING)
                .build();
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(get("/api/v1/payments/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentReference").value("PAY-TEST-123"));
    }

    @Test
    void getPaymentById_NonExistingPayment_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/payments/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PAYMENT_NOT_FOUND"));
    }

    @Test
    void processPayment_PendingPayment_ReturnsProcessed() throws Exception {
        // Create a payment first
        Payment payment = Payment.builder()
                .paymentReference("PAY-PROCESS-123")
                .idempotencyKey("test-key-process")
                .merchantId("merchant-001")
                .amount(new BigDecimal("75.00"))
                .currency(Currency.GBP)
                .status(PaymentStatus.PENDING)
                .build();
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(post("/api/v1/payments/" + saved.getId() + "/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value(anyOf(
                        is("COMPLETED"), is("FAILED")
                )));
    }

    @Test
    void cancelPayment_PendingPayment_ReturnsCancelled() throws Exception {
        // Create a payment first
        Payment payment = Payment.builder()
                .paymentReference("PAY-CANCEL-123")
                .idempotencyKey("test-key-cancel")
                .merchantId("merchant-001")
                .amount(new BigDecimal("25.00"))
                .currency(Currency.TRY)
                .status(PaymentStatus.PENDING)
                .build();
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(post("/api/v1/payments/" + saved.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }
}