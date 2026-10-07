package com.leetcode.comments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponse {
    private int id;
    @JsonProperty("post_id") private int postId;
    @JsonProperty("comment_id") private int commentId;
    @JsonProperty("user_id") private String userId;
    private String content;
    @JsonProperty("created_at") private Instant createdAt;
}
