package com.payment_service.util;

import java.util.UUID;

public class PaymentReferenceGenerator {

    private static final String PREFIX = "PAY";

    public static String generate() {
        // Generate format: PAY-{timestamp}-{random}
        // Example: PAY-20231203-A3F2B
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000); // Unix timestamp
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("%s-%s-%s", PREFIX, timestamp, random);
    }
}