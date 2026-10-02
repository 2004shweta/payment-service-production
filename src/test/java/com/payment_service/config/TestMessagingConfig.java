package com.payment_service.config;

import com.payment_service.service.PaymentEventPublisher;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestMessagingConfig {

    @Bean
    @Primary
    public RabbitTemplate testRabbitTemplate() {
        return Mockito.mock(RabbitTemplate.class);
    }

    @Bean
    @Primary
    public PaymentEventPublisher testPaymentEventPublisher() {
        return Mockito.mock(PaymentEventPublisher.class);
    }
}