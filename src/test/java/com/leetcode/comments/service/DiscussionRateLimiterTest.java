package com.leetcode.comments.service;

import com.leetcode.comments.exception.RateLimitExceededException;
import com.leetcode.comments.service.impl.InMemoryDiscussionRateLimiter;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscussionRateLimiterTest {
    @Test
    void allowsFiveWritesAndRejectsTheSixthForOneUser() {
        DiscussionRateLimiter limiter = new InMemoryDiscussionRateLimiter();

        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> limiter.checkAllowed("u1"));
        }

        assertThrows(RateLimitExceededException.class,
                () -> limiter.checkAllowed("u1"));
    }

    @Test
    void allowsAWriteWhenTheOldestTimestampExpires() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        DiscussionRateLimiter limiter = new InMemoryDiscussionRateLimiter(clock);

        for (int i = 0; i < 5; i++) {
            limiter.checkAllowed("u1");
        }

        clock.advanceSeconds(60);

        assertDoesNotThrow(() -> limiter.checkAllowed("u1"));
    }

    @Test
    void keepsLimitsIndependentBetweenUsers() {
        DiscussionRateLimiter limiter = new InMemoryDiscussionRateLimiter();

        for (int i = 0; i < 5; i++) {
            limiter.checkAllowed("u1");
        }

        assertDoesNotThrow(() -> limiter.checkAllowed("u2"));
    }

    @Test
    void allowsOnlyFiveConcurrentWritesForOneUser() throws InterruptedException {
        DiscussionRateLimiter limiter = new InMemoryDiscussionRateLimiter();
        int attempts = 20;
        ExecutorService executor = Executors.newFixedThreadPool(attempts);
        CountDownLatch ready = new CountDownLatch(attempts);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger allowed = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        for (int i = 0; i < attempts; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    limiter.checkAllowed("u1");
                    allowed.incrementAndGet();
                } catch (RateLimitExceededException exception) {
                    rejected.incrementAndGet();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(5, allowed.get());
        assertEquals(15, rejected.get());
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        private void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
