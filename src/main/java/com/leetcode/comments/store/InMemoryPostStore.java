package com.leetcode.comments.store;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class InMemoryPostStore implements PostStore {
    private final Map<Integer, String> posts = Map.of(10, "Welcome to LeetCode", 20, "System Design Guide");
    public boolean exists(int postId) { return posts.containsKey(postId); }
}
