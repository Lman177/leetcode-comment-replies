package com.leetcode.comments.controller;

import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateCommentRequest;
import com.leetcode.comments.model.ReplyResponse;
import com.leetcode.comments.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/post/{postId}/comment/add")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
            @PathVariable int postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return commentService.addComment(postId, request);
    }

    @PostMapping("/post/{postId}/comment/{commentId}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    public ReplyResponse addReply(
            @PathVariable int postId,
            @PathVariable int commentId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return commentService.addReply(postId, commentId, request);
    }

    @GetMapping("/post/{postId}/comments")
    public List<CommentResponse> getComments(@PathVariable int postId) {
        return commentService.getComments(postId);
    }
}
