package com.leetcode.comments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @JsonProperty("user_id")
        @NotBlank(message = "user_id must be a non-blank string")
        @Size(max = 255, message = "user_id must not exceed 255 characters")
        String userId,

        @NotBlank(message = "content must be a non-blank string")
        @Size(max = 4000, message = "content must not exceed 4000 characters")
        String content
) {
}
