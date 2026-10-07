package com.leetcode.comments.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
public record CreateRequest(@JsonProperty("user_id") JsonNode userId, JsonNode content) {}
