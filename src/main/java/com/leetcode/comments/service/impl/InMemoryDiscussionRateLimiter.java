package com.leetcode.comments.service.impl;

import com.leetcode.comments.exception.RateLimitExceededException;
import com.leetcode.comments.service.DiscussionRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class InMemoryDiscussionRateLimiter implements DiscussionRateLimiter {
    private static final int MAX_WRITES = 5;
    private static final Duration WINDOW = Duration.ofSeconds(60);

    private final ConcurrentMap<String, Deque<Instant>> writesByUser =
            new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryDiscussionRateLimiter() {
        this(Clock.systemUTC());
    }

    public InMemoryDiscussionRateLimiter(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void checkAllowed(String userId) {
        Instant now = clock.instant();
        Instant cutoff = now.minus(WINDOW);

        writesByUser.compute(userId, (key, timestamps) -> {
            Deque<Instant> recentWrites = timestamps == null
                    ? new ArrayDeque<>()
                    : timestamps;
            int expiredWrites = 0;

            while (!recentWrites.isEmpty()
                    && !recentWrites.peekFirst().isAfter(cutoff)) {
                recentWrites.removeFirst();
                expiredWrites++;
            }

            if (recentWrites.size() >= MAX_WRITES) {
                log.warn(
                        "Rate limit rejected: userId={}, activeWrites={}, windowSeconds={}",
                        userId,
                        recentWrites.size(),
                        WINDOW.toSeconds()
                );
                throw new RateLimitExceededException(
                        "User " + userId
                                + " may create at most 5 comments or replies per 60 seconds"
                );
            }

            recentWrites.addLast(now);
            log.debug(
                    "Rate limit allowed: userId={}, activeWrites={}, expiredWrites={}",
                    userId,
                    recentWrites.size(),
                    expiredWrites
            );
            return recentWrites;
        });
    }
}
