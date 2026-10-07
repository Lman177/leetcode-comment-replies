package com.leetcode.comments.service;

public interface DiscussionRateLimiter {
    void checkAllowed(String userId);
}
