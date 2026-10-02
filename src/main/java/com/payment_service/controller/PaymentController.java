package com.payment_service.controller;

import com.payment_service.dto.ApiResponse;
import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.model.PaymentStatus;
import com.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create a new payment
     * POST /api/v1/payments
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        log.info("Received payment creation request for merchant: {}", request.getMerchantId());

        PaymentResponse payment = paymentService.createPayment(request);
        ApiResponse<PaymentResponse> response = ApiResponse.success(
                "Payment created successfully",
                payment
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get payment by ID
     * GET /api/v1/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        log.debug("Fetching payment by id: {}", id);

        PaymentResponse payment = paymentService.getPaymentById(id);
        ApiResponse<PaymentResponse> response = ApiResponse.success(payment);

        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by reference
     * GET /api/v1/payments/reference/{reference}
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByReference(
            @PathVariable String reference) {

        log.debug("Fetching payment by reference: {}", reference);

        PaymentResponse payment = paymentService.getPaymentByReference(reference);
        ApiResponse<PaymentResponse> response = ApiResponse.success(payment);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments for a merchant
     * GET /api/v1/payments/merchant/{merchantId}
     */
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByMerchant(
            @PathVariable String merchantId) {

        log.debug("Fetching payments for merchant: {}", merchantId);

        List<PaymentResponse> payments = paymentService.getPaymentsByMerchant(merchantId);
        ApiResponse<List<PaymentResponse>> response = ApiResponse.success(payments);

        return ResponseEntity.ok(response);
    }

    /**
     * Get payments by merchant and status
     * GET /api/v1/payments/merchant/{merchantId}/status/{status}
     */
    @GetMapping("/merchant/{merchantId}/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByMerchantAndStatus(
            @PathVariable String merchantId,
            @PathVariable PaymentStatus status) {

        log.debug("Fetching payments for merchant: {} with status: {}", merchantId, status);

        List<PaymentResponse> payments = paymentService.getPaymentsByMerchantAndStatus(merchantId, status);
        ApiResponse<List<PaymentResponse>> response = ApiResponse.success(payments);

        return ResponseEntity.ok(response);
    }

    /**
     * Process a payment
     * POST /api/v1/payments/{id}/process
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@PathVariable Long id) {
        log.info("Processing payment: {}", id);

        PaymentResponse payment = paymentService.processPayment(id);
        ApiResponse<PaymentResponse> response = ApiResponse.success(
                "Payment processed",
                payment
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Retry a failed payment
     * POST /api/v1/payments/{id}/retry
     */
    @PostMapping("/{id}/retry")
    public ResponseEntity<ApiResponse<PaymentResponse>> retryPayment(@PathVariable Long id) {
        log.info("Retrying payment: {}", id);

        PaymentResponse payment = paymentService.retryPayment(id);
        ApiResponse<PaymentResponse> response = ApiResponse.success(
                "Payment retry initiated",
                payment
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a payment
     * POST /api/v1/payments/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(@PathVariable Long id) {
        log.info("Cancelling payment: {}", id);

        PaymentResponse payment = paymentService.cancelPayment(id);
        ApiResponse<PaymentResponse> response = ApiResponse.success(
                "Payment cancelled",
                payment
        );

        return ResponseEntity.ok(response);
    }
}