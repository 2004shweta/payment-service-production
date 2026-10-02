package com.payment_service.model;

public enum PaymentStatus {
    PENDING,      // Ödeme oluşturuldu, işleme alınmadı
    PROCESSING,   // Ödeme işleniyor
    COMPLETED,    // Ödeme başarılı
    FAILED,       // Ödeme başarısız
    CANCELLED     // Ödeme iptal edildi
}