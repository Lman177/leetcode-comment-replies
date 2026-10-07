package com.leetcode.comments.service.impl;

import com.leetcode.comments.service.DiscussionRateLimiter;
import org.springframework.stereotype.Service;

@Service
public class InMemoryDiscussionRateLimiter implements DiscussionRateLimiter {
    // TODO: Store each user's recent write timestamps in memory.
    // TODO: Add concurrency control and remove expired timestamps.

    @Override
    public void checkAllowed(String userId) {
        // TODO: Allow at most 5 comment/reply writes per user in a rolling
        //       60-second window. Throw RateLimitExceededException when denied.
        throw new UnsupportedOperationException("TODO: implement discussion rate limiter");
    }
}
