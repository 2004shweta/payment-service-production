package com.payment_service.service;

import com.payment_service.model.IdempotencyKey;
import com.payment_service.model.Payment;
import com.payment_service.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final RedisService redisService;

    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
    private static final long IDEMPOTENCY_TTL_HOURS = 24;

    /**
     * Check if idempotency key exists (first in Redis, then DB)
     * @param key Idempotency key
     * @return Optional of IdempotencyKey if exists and valid
     */
    public Optional<IdempotencyKey> checkIdempotencyKey(String key) {
        log.debug("Checking idempotency key: {}", key);

        String redisKey = IDEMPOTENCY_KEY_PREFIX + key;

        // First check Redis (fast path)
        IdempotencyKey cached = redisService.get(redisKey, IdempotencyKey.class);
        if (cached != null) {
            log.info("Idempotency key found in Redis cache: {}", key);
            return Optional.of(cached);
        }

        // Then check database (slow path)
        Optional<IdempotencyKey> existingKey = idempotencyKeyRepository.findByIdempotencyKey(key);

        if (existingKey.isPresent()) {
            IdempotencyKey idempotencyKey = existingKey.get();

            // Check if expired
            if (idempotencyKey.isExpired()) {
                log.info("Idempotency key expired: {}", key);
                idempotencyKeyRepository.delete(idempotencyKey);
                redisService.delete(redisKey);
                return Optional.empty();
            }

            // Cache in Redis for future lookups
            redisService.set(redisKey, idempotencyKey, IDEMPOTENCY_TTL_HOURS, TimeUnit.HOURS);

            log.info("Duplicate payment request detected with idempotency key: {}", key);
            return existingKey;
        }

        return Optional.empty();
    }

    /**
     * Store idempotency key (both in DB and Redis)
     * @param payment Payment entity
     */
    @Transactional
    public void storeIdempotencyKey(Payment payment) {
        log.debug("Storing idempotency key: {} for payment: {}",
                payment.getIdempotencyKey(), payment.getPaymentReference());

        // Store in database
        IdempotencyKey idempotencyKey = IdempotencyKey.builder()
                .idempotencyKey(payment.getIdempotencyKey())
                .paymentId(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .build();

        IdempotencyKey saved = idempotencyKeyRepository.save(idempotencyKey);

        // Cache in Redis
        String redisKey = IDEMPOTENCY_KEY_PREFIX + payment.getIdempotencyKey();
        redisService.set(redisKey, saved, IDEMPOTENCY_TTL_HOURS, TimeUnit.HOURS);

        log.info("Idempotency key stored in DB and Redis: {}", payment.getIdempotencyKey());
    }

    /**
     * Clean up expired idempotency keys from database
     * Redis keys will expire automatically
     */
    @Transactional
    public int cleanupExpiredKeys() {
        log.info("Cleaning up expired idempotency keys from database");
        int deletedCount = idempotencyKeyRepository.deleteExpiredKeys(LocalDateTime.now());
        log.info("Deleted {} expired idempotency keys from database", deletedCount);
        return deletedCount;
    }
}