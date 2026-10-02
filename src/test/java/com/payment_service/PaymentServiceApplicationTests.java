package com.payment_service;

import com.payment_service.config.TestMessagingConfig;
import com.payment_service.config.TestRedisConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestMessagingConfig.class, TestRedisConfig.class})
@Disabled("Context loading test disabled")
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}