package com.leetcode.comments.service;

import com.leetcode.comments.exception.RateLimitExceededException;
import com.leetcode.comments.service.impl.InMemoryDiscussionRateLimiter;
import org.junit.jupiter.api.Test;

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
}
